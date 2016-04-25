package org.zalando.planb.revocation.config.properties;

import com.datastax.driver.core.ConsistencyLevel;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CassandraProperties}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class CassandraPropertiesTest {

    @Test
    public void testSetters() {
        CassandraProperties properties = new CassandraProperties();

        String keyspace = "revocation_v2";
        String contactPoints = "127.0.0.1";
        String clusterName = "Live Cluster";
        int port = 9012;
        ConsistencyLevel writeConsistencyLevel = ConsistencyLevel.ALL;
        ConsistencyLevel readConsistencyLevel = ConsistencyLevel.ANY;
        String username = "test";
        String password = "c08audshcf087ahdc";
        int maxTimeDelta = 300;

        properties.setKeyspace(keyspace);
        properties.setContactPoints(contactPoints);
        properties.setClusterName(clusterName);
        properties.setPort(port);
        properties.setWriteConsistencyLevel(writeConsistencyLevel);
        properties.setReadConsistencyLevel(readConsistencyLevel);
        properties.setUsername(Optional.of(username));
        properties.setPassword(Optional.of(password));
        properties.setMaxTimeDelta(maxTimeDelta);

        assertThat(properties.getKeyspace()).isEqualTo(keyspace);
        assertThat(properties.getContactPoints()).isEqualTo(contactPoints);
        assertThat(properties.getClusterName()).isEqualTo(clusterName);
        assertThat(properties.getPort()).isEqualTo(port);
        assertThat(properties.getWriteConsistencyLevel()).isEqualTo(writeConsistencyLevel);
        assertThat(properties.getReadConsistencyLevel()).isEqualTo(readConsistencyLevel);
        assertThat(properties.getUsername().get()).isEqualTo(username);
        assertThat(properties.getPassword().get()).isEqualTo(password);
        assertThat(properties.getMaxTimeDelta()).isEqualTo(maxTimeDelta);
    }
}