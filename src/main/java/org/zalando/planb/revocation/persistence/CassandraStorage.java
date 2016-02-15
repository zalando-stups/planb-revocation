package org.zalando.planb.revocation.persistence;

import com.datastax.driver.core.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.planb.revocation.LocalDateFormatter;

import java.io.IOException;
import java.util.*;

/**
 * Created by jmussler on 11.02.16.
 */
public class CassandraStorage implements RevocationStore {

    private static final Logger LOG = LoggerFactory.getLogger(CassandraStorage.class);

    private final Session session;

    private final PreparedStatement getFrom;
    private final PreparedStatement storeRevocation;

    private final Map<String, RevocationDataMapper> dataMappers = new HashMap<>();

    private static class GlobalMapper implements RevocationDataMapper {
        @Override
        public RevocationData get(String data) throws IOException {
            GlobalRevocation r = mapper.readValue(data, GlobalRevocation.class);
            return r;
        }
    }

    private static class ClaimMapper implements RevocationDataMapper {
        @Override
        public RevocationData get(String data) throws IOException {
            ClaimRevocation r = mapper.readValue(data, ClaimRevocation.class);
            return r;
        }
    }

    private static class TokenMapper implements RevocationDataMapper {
        @Override
        public RevocationData get(String data) throws IOException {
            TokenRevocation r = mapper.readValue(data, TokenRevocation.class);
            return r;
        }
    }

    public CassandraStorage(Session session) {
        this.session = session;

        dataMappers.put("TOKEN", new TokenMapper());
        dataMappers.put("GLOBAL", new GlobalMapper());
        dataMappers.put("CLAIM", new ClaimMapper());

        getFrom = session.prepare("SELECT FROM revocation.revocation WHERE (bucket_date = ? AND bucket_interval = ?) AND revoked_at > ?");
        storeRevocation = session.prepare("INSERT INTO revocation.revocation(bucket_date, bucket_interval, revocation_type, revocation_data, revoked_by, revoked_at) VALUES (?, ?, ?, ?, ?, ?)");
    }

    final static ObjectMapper mapper = new ObjectMapper();

    static class Bucket {
        public String date;
        public String interval;

        Bucket(String d, String i) {
            date = d;
            interval = i;
        }
    }

    protected static List<Bucket> getBuckets(final long from, final long currentTime) {
        List<Bucket> buckets = new ArrayList<>();
        long delta = currentTime - from;

        while (delta > 0) {
            String bucket_date = LocalDateFormatter.get().format(new Date(currentTime-delta));
            String bucket_interval = "" + getInterval(currentTime-delta);

            buckets.add(new Bucket(bucket_date, bucket_interval));

            delta -= 8*60*60*1000; // 8 hour buckets
        }
        return buckets;
    }

    @Override
    public Collection<StoredRevocation> getRevocations(final long from) {

        Collection<StoredRevocation> revocations = new ArrayList<>();

        for(Bucket b : getBuckets(from, System.currentTimeMillis())) {

            LOG.info("Selecting bucket: {} {}", b.date, b.interval);

            ResultSet rs = session.execute(getFrom.bind(b.date, b.interval, from));
            List<Row> rows = rs.all();

            for(Row r : rows) {
                try {
                    String type = r.getString("revocation_type");
                    String unmapped_data = r.getString("revocation_data");
                    RevocationData data = dataMappers.get(type).get(unmapped_data);
                    StoredRevocation revocation = new StoredRevocation(data, type, r.getString("revoked_by"));
                    revocation.setRevocedAt(r.getLong("revoked_at"));
                    revocations.add(revocation);
                }
                catch(IOException ex) {
                    LOG.error("Failed to read revocation");
                }
            }
        }

        return revocations;
    }

    private static long getInterval(long timestamp) {
        long hours = timestamp / 1000 / 60 / 60;
        long segment = (hours % 24) / 8;
        return segment;
    }

    @Override
    public boolean storeRevocation(final StoredRevocation revocation) {
        String date = LocalDateFormatter.get().format(new Date(revocation.getRevocedAt()));
        String interval = "" + getInterval(revocation.getRevocedAt());
        try {
            String data = mapper.writeValueAsString(revocation.getData());
            BoundStatement bs = storeRevocation.bind(date, interval, revocation.getType(), data, revocation.getRevocedBy(), revocation.getRevocedAt());
            session.execute(bs);
            return true;
        }
        catch(JsonProcessingException ex) {
            LOG.error("Failed to serialize json");
            return false;
        }
    }
}
