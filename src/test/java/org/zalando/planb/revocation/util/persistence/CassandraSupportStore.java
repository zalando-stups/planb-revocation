package org.zalando.planb.revocation.util.persistence;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.slf4j.Logger;
import org.zalando.planb.revocation.util.LocalDateFormatter;
import org.zalando.planb.revocation.util.UnixTimestamp;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gt;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Utility Interface to Cassandra cluster, to get audit information.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class CassandraSupportStore {

    private static final Logger log = getLogger(CassandraSupportStore.class);

    /*
     * Tables and queries for revocation_schema.cql
     */
    private static final String REVOCATION_TABLE = "revocation";

    private static final String REFRESH_TABLE = "refresh";

    private static final RegularStatement SELECT_REVOCATION_REVOKED_BY =
            QueryBuilder.select()
                    .column("revoked_by")
                    .from(REVOCATION_TABLE)
                    .where(eq("bucket_date", bindMarker()))
                    .and(eq("bucket_interval", bindMarker())).and(
                    gt("revoked_at", bindMarker()));

    private static final RegularStatement SELECT_REFRESH_CREATED_BY =
            QueryBuilder.select()
                    .column("created_by")
                    .from(REFRESH_TABLE)
                    .where(eq("refresh_year", bindMarker())).limit(1);

    // Queries for cleaning up between tests.
    private static final RegularStatement CLEANUP_REVOCATIONS = QueryBuilder.truncate(REVOCATION_TABLE);

    private static final RegularStatement CLEANUP_REFRESH = QueryBuilder.truncate(REFRESH_TABLE);

    private static final int BUCKET_LENGTH = 8 * 60 * 60; // 8 Hours per bucket/row

    private final Session session;

    private final int maxTimeDelta;

    private final PreparedStatement getRevokedBy;

    private final PreparedStatement getCreatedBy;

    private final PreparedStatement cleanupRevocations;

    private final PreparedStatement cleanupRefresh;

    /**
     * Constructs a new instance configured with the provided {@code session} and {@code maxTimeDelta}.
     *
     * @param session      session configured to a Cassandra cluster
     * @param read         consistency level for SELECT queries
     * @param write        consistency level for TRUNCATE queries
     * @param maxTimeDelta maximum time span limit to get revocations, in seconds
     */
    public CassandraSupportStore(final Session session, final ConsistencyLevel read, final ConsistencyLevel write,
                                 final int maxTimeDelta) {
        this.session = session;
        this.maxTimeDelta = maxTimeDelta;

        getRevokedBy = session.prepare(SELECT_REVOCATION_REVOKED_BY).setConsistencyLevel(read);
        getCreatedBy = session.prepare(SELECT_REFRESH_CREATED_BY).setConsistencyLevel(read);
        cleanupRevocations = session.prepare(CLEANUP_REVOCATIONS).setConsistencyLevel(write);
        cleanupRefresh = session.prepare(CLEANUP_REFRESH).setConsistencyLevel(write);
    }

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

    protected static int getInterval(final int timestamp) {
        int hours = timestamp / (60 * 60);
        return (hours % 24) / 8;
    }

    /**
     * Returns the audit information of who created revocations, from the specified UNIX Timestamp.
     *
     * @return a collection of strings with the audit information.
     */
    public Collection<String> getRevokedBy(final int from) {

        Collection<String> revokedByValues = new LinkedList<>();

        int currentTime = UnixTimestamp.now();
        if ((currentTime - from) > maxTimeDelta) {
            // avoid erroneous query of too many buckets
            throw new IllegalArgumentException("'from' timestamp is too old!");
        }

        for (Bucket b : getBuckets(from, currentTime)) {

            ResultSet rs = session.execute(getRevokedBy.bind(b.date, b.interval, from));

            List<Row> rows = rs.all();
            revokedByValues.addAll(rows.stream().map(r -> r.getString("revoked_by")).collect(Collectors.toList()));
        }

        return revokedByValues;
    }

    /**
     * Returns the audit information of who created the latest refresh notification.
     *
     * @return a string with the audit information.
     */
    public String getCreatedBy() {
        int yearBucket = LocalDate.now(ZoneId.of("UTC")).getYear();

        // TODO Include the case when it's the beginning of the year (2 buckets needed)
        ResultSet rs = session.execute(getCreatedBy.bind(yearBucket));

        // No refreshes returns null
        if (rs.isExhausted()) {
            return null;
        }

        // Only the first, although the result set should be 1 already.
        Row first = rs.one();

        return first.getString("created_by");
    }

    /**
     * Cleans up the revocation and refresh tables.
     *
     * <p>This is useful between executions of integration tests.</p>
     */
    public void cleanup() {

        session.execute(cleanupRevocations.bind());
        session.execute(cleanupRefresh.bind());
    }

    static class Bucket {
        public String date;
        public int interval;

        Bucket(final String d, final int i) {
            date = d;
            interval = i;
        }
    }
}
