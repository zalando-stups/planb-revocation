package org.zalando.planb.revocation.persistence;

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gt;
import static com.datastax.driver.core.querybuilder.QueryBuilder.now;

import java.io.IOException;

import java.time.LocalDate;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.util.LocalDateFormatter;
import org.zalando.planb.revocation.util.UnixTimestamp;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Interface to Cassandra cluster.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Slf4j
public class CassandraStore implements RevocationStore {

    /*
     * Tables and queries for revocation_schema.cql
     */

    private static final String REVOCATION_TABLE = "revocation";

    private static final String REFRESH_TABLE = "refresh";

    private static final RegularStatement SELECT_REVOCATION = QueryBuilder.select().column("revocation_type")
                                                                          .column("revocation_data")
                                                                          .column("revoked_by").column("revoked_at")
                                                                          .column("bucket_uuid").from(REVOCATION_TABLE)
                                                                          .where(eq("bucket_date", bindMarker()))
                                                                          .and(eq("bucket_interval", bindMarker())).and(
                                                                              gt("revoked_at", bindMarker()));

    private static final RegularStatement INSERT_REVOCATION = QueryBuilder.insertInto(REVOCATION_TABLE)
                                                                          .value("bucket_date", bindMarker())
                                                                          .value("bucket_interval", bindMarker())
                                                                          .value("revocation_type", bindMarker())
                                                                          .value("revocation_data", bindMarker())
                                                                          .value("revoked_by", bindMarker())
                                                                          .value("revoked_at", bindMarker()).value(
                                                                              "bucket_uuid", now());

    private static final RegularStatement INSERT_REFRESH = QueryBuilder.insertInto(REFRESH_TABLE)
                                                                       .value("refresh_year", bindMarker())
                                                                       .value("refresh_ts", bindMarker())
                                                                       .value("refresh_from", bindMarker()).value(
                                                                           "created_by", bindMarker());

    private static final RegularStatement SELECT_REFRESH = QueryBuilder.select().column("refresh_from")
                                                                       .column("refresh_ts").column("created_by")
                                                                       .column("refresh_year").from(REFRESH_TABLE)
                                                                       .where(eq("refresh_year", bindMarker())).limit(
                                                                           1);

    private final Session session;

    private final int maxTimeDelta;

    private final PreparedStatement getFrom;

    private final PreparedStatement insertRevocation;

    private final PreparedStatement getRefresh;

    private final PreparedStatement storeRefresh;

    private final Map<RevocationType, RevocationDataMapper> dataMappers = new EnumMap<>(RevocationType.class);

    private static class GlobalMapper implements RevocationDataMapper {
        @Override
        public RevocationData get(final String data) throws IOException {
            return MAPPER.readValue(data, StoredGlobal.class);
        }
    }

    private static class ClaimMapper implements RevocationDataMapper {
        @Override
        public RevocationData get(final String data) throws IOException {
            return MAPPER.readValue(data, StoredClaim.class);
        }
    }

    private static class TokenMapper implements RevocationDataMapper {
        @Override
        public RevocationData get(final String data) throws IOException {
            return MAPPER.readValue(data, StoredToken.class);
        }
    }

    /**
     * Constructs a new instance configured with the provided {@code session} and {@code maxTimeDelta}.
     *
     * @param  session       session configured to a Cassandra cluster
     * @param  read          consistency level for SELECT queries
     * @param  write         consistency level for INSERT queries
     * @param  maxTimeDelta  maximum time span limit to get revocations, in seconds
     */
    public CassandraStore(final Session session, final ConsistencyLevel read, final ConsistencyLevel write,
            final int maxTimeDelta) {
        this.session = session;
        this.maxTimeDelta = maxTimeDelta;

        dataMappers.put(RevocationType.TOKEN, new TokenMapper());
        dataMappers.put(RevocationType.GLOBAL, new GlobalMapper());
        dataMappers.put(RevocationType.CLAIM, new ClaimMapper());

        getFrom = session.prepare(SELECT_REVOCATION).setConsistencyLevel(read);
        insertRevocation = session.prepare(INSERT_REVOCATION).setConsistencyLevel(write);
        getRefresh = session.prepare(SELECT_REFRESH).setConsistencyLevel(read);
        storeRefresh = session.prepare(INSERT_REFRESH).setConsistencyLevel(write);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static class Bucket {
        public String date;
        public int interval;

        Bucket(final String d, final int i) {
            date = d;
            interval = i;
        }
    }

    private static final int BUCKET_LENGTH = 8 * 60 * 60; // 8 Hours per bucket/row

    protected static List<Bucket> getBuckets(int from, final int currentTime) {
        List<Bucket> buckets = new ArrayList<>();

        final int maxTime = ((currentTime / BUCKET_LENGTH) * BUCKET_LENGTH) + BUCKET_LENGTH;
        log.debug("{} {}", currentTime, maxTime);

        do {
            String bucketDate = LocalDateFormatter.get().format(new Date(((long) from) * 1000));
            int bucketInterval = getInterval(from);

            buckets.add(new Bucket(bucketDate, bucketInterval));

            from += BUCKET_LENGTH;

        } while (from < maxTime);

        return buckets;
    }

    @Override
    public Collection<StoredRevocation> getRevocations(final int from) {

        Collection<StoredRevocation> revocations = new ArrayList<>();

        int currentTime = UnixTimestamp.now();
        if ((currentTime - from) > maxTimeDelta) {

            // avoid erroneous query of too many buckets
            throw new IllegalArgumentException("'from' timestamp is too old!");
        }

        for (Bucket b : getBuckets(from, currentTime)) {

            ResultSet rs = session.execute(getFrom.bind(b.date, b.interval, from));
            List<Row> rows = rs.all();

            for (Row r : rows) {
                try {
                    RevocationType type = RevocationType.valueOf(r.getString("revocation_type").toUpperCase());
                    String unmappedData = r.getString("revocation_data");
                    RevocationData data = dataMappers.get(type).get(unmappedData);
                    StoredRevocation revocation = new StoredRevocation(data, type, r.getString("revoked_by"));
                    revocation.setRevokedAt(r.getInt("revoked_at"));
                    revocations.add(revocation);
                } catch (IOException ex) {
                    log.error("Failed to read revocation", ex);
                }
            }
        }

        return revocations;
    }

    protected static int getInterval(final int timestamp) {
        int hours = timestamp / (60 * 60);
        return (hours % 24) / 8;
    }

    @Override
    public boolean storeRevocation(final StoredRevocation revocation) {
        String date = LocalDateFormatter.get().format(new Date(((long) revocation.getRevokedAt()) * 1000));

        int interval = getInterval(revocation.getRevokedAt());
        try {
            String data = MAPPER.writeValueAsString(revocation.getData());
            log.debug("Storing in bucket: {} {} {}", date, interval, data);

            BoundStatement bs = insertRevocation.bind(date, interval, revocation.getType().name(), data,
                    revocation.getRevokedBy(), revocation.getRevokedAt());

            session.execute(bs);
            return true;
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize json", ex);
            return false;
        }
    }

    @Override
    public Refresh getRefresh() {
        int yearBucket = LocalDate.now(ZoneId.of("UTC")).getYear();

        // TODO Include the case when it's the beginning of the year (2 buckets needed)
        ResultSet rs = session.execute(getRefresh.bind(yearBucket));

        // No refreshes returns null
        if (rs.isExhausted()) {
            return null;
        }

        // Only the first, although the result set should be 1 already.
        Row first = rs.one();

        return Refresh.builder().refreshFrom(first.getInt("refresh_from")).refreshTimestamp(first.getInt("refresh_ts"))
                      .build();
    }

    @Override
    public boolean storeRefresh(final int from) {

        // TODO Include refresh_by
        int yearBucket = LocalDate.now(ZoneId.of("UTC")).getYear();
        BoundStatement statement = storeRefresh.bind(yearBucket, UnixTimestamp.now(), from, "");
        session.execute(statement);

        return true;
    }
}
