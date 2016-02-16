package org.zalando.planb.revocation.persistence;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.datastax.driver.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.zalando.planb.revocation.LocalDateFormatter;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.domain.RevocationType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by jmussler on 11.02.16.
 */
@Slf4j
public class CassandraStorage implements RevocationStore {

    private final Session session;

    private final PreparedStatement getFrom;

    private final PreparedStatement storeRevocation;

    private final Map<RevocationType, RevocationDataMapper> dataMappers = new EnumMap<>(RevocationType.class);

    private static class GlobalMapper implements RevocationDataMapper {
        @Override
        public RevocationData get(final String data) throws IOException {
            StoredGlobal r = mapper.readValue(data, StoredGlobal.class);
            return r;
        }
    }

    private static class ClaimMapper implements RevocationDataMapper {
        @Override
        public RevocationData get(final String data) throws IOException {
            StoredClaim r = mapper.readValue(data, StoredClaim.class);
            return r;
        }
    }

    private static class TokenMapper implements RevocationDataMapper {
        @Override
        public RevocationData get(final String data) throws IOException {
            StoredToken r = mapper.readValue(data, StoredToken.class);
            return r;
        }
    }

    public CassandraStorage(CassandraProperties cassandraProperties) {
        Cluster cluster = Cluster.builder().addContactPoints(cassandraProperties.getContactPoints().split(","))
                .withClusterName(cassandraProperties.getClusterName())
                .withPort(cassandraProperties.getPort()).build();

        session = cluster.connect(cassandraProperties.getKeyspace());

        dataMappers.put(RevocationType.TOKEN, new TokenMapper());
        dataMappers.put(RevocationType.GLOBAL, new GlobalMapper());
        dataMappers.put(RevocationType.CLAIM, new ClaimMapper());

        getFrom = session.prepare("SELECT revocation_type, revocation_data, revoked_by, revoked_at FROM revocation" +
                ".revocation WHERE (bucket_date = ?) AND (bucket_interval = ?) AND (revoked_at > ?)");
        storeRevocation = session.prepare(
                "INSERT INTO revocation.revocation(bucket_date, bucket_interval, revocation_type, revocation_data, revoked_by, revoked_at) VALUES (?, ?, ?, ?, ?, ?)");
    }

    static final ObjectMapper mapper = new ObjectMapper();

    static class Bucket {
        public String date;
        public String interval;

        Bucket(final String d, final String i) {
            date = d;
            interval = i;
        }
    }

    protected static List<Bucket> getBuckets(final long from, final long currentTime) {
        List<Bucket> buckets = new ArrayList<>();
        long delta = currentTime - from;

        while (delta > 0) {
            String bucket_date = LocalDateFormatter.get().format(new Date(currentTime - delta));
            String bucket_interval = "" + getInterval(currentTime - delta);

            buckets.add(new Bucket(bucket_date, bucket_interval));

            delta -= 8 * 60 * 60 * 1000; // 8 hour buckets
        }

        return buckets;
    }

    @Override
    public Collection<StoredRevocation> getRevocations(final Long from) {

        Collection<StoredRevocation> revocations = new ArrayList<>();

        for (Bucket b : getBuckets(from, System.currentTimeMillis())) {

            log.info("Selecting bucket: {} {}", b.date, b.interval);

            ResultSet rs = session.execute(getFrom.bind(b.date, b.interval, from));
            List<Row> rows = rs.all();

            for (Row r : rows) {
                try {
                    RevocationType type = RevocationType.valueOf(r.getString("revocation_type").toUpperCase());
                    String unmapped_data = r.getString("revocation_data");
                    RevocationData data = dataMappers.get(type).get(unmapped_data);
                    StoredRevocation revocation = new StoredRevocation(data, type, r.getString("revoked_by"));
                    revocation.setRevokedAt(r.getLong("revoked_at"));
                    revocations.add(revocation);
                } catch (IOException ex) {
                    log.error("Failed to read revocation");
                }
            }
        }

        return revocations;
    }

    private static long getInterval(final long timestamp) {
        long hours = timestamp / 1000 / 60 / 60;
        long segment = (hours % 24) / 8;
        return segment;
    }

    @Override
    public boolean storeRevocation(final StoredRevocation revocation) {
        String date = LocalDateFormatter.get().format(new Date(revocation.getRevokedAt()));
        String interval = "" + getInterval(revocation.getRevokedAt());
        try {
            String data = mapper.writeValueAsString(revocation.getData());
            BoundStatement bs = storeRevocation.bind(date, interval, revocation.getType(), data,
                    revocation.getRevokedBy(), revocation.getRevokedAt());
            session.execute(bs);
            return true;
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize json");
            return false;
        }
    }
}
