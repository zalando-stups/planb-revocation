package org.zalando.planb.revocation.config.properties;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ProtocolOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Optional;

import static com.datastax.driver.core.ConsistencyLevel.EACH_QUORUM;
import static com.datastax.driver.core.ConsistencyLevel.ONE;

/**
 * Properties used to configure a Cassandra cluster data source.
 * <p>
 * <p>The following properties are used and can be defined through <a
 * href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html">Spring
 * Configuration</a>:</p>
 * <p>
 * <ul>
 * <li>{@code cassandra.keyspace} - Keyspace in the Cassandra cluster. Default value is {@code revocation} (See the
 * provided <a href="https://github.com/zalando/planb-revocation/blob/master/revocation_schema.cql">CQL
 * Script</a>);</li>
 * <li>{@code cassandra.contactPoints} - Comma separated list of hosts;</li>
 * <li>{@code cassandra.clusterName} - Name of the Cassandra cluster. Default value is {@code Cassandra};</li>
 * <li>{@code cassandra.port} - Port of the Cassandra cluster. Default value is {@code 9042};</li>
 * <li>{@code cassandra.writeConsistencyLevel} - Consistency level for write operations. Default value is {@code ONE};
 * </li>
 * <li>{@code cassandra.readConsistencyLevel} - Consistency level for read operations. Default value is
 * {@code EACH_QUORUM};</li>
 * <li>{@code cassandra.username} - User account to access the Cassandra cluster. Default value is empty;</li>
 * <li>{@code cassandra.password} - User password to access the Cassandra cluster. Default value is empty;</li>
 * <li>{@code cassandra.maxTimeDelta} - The maximum time span limit to get revocations, in seconds. Default value is
 * {@code 2678400}, meaning that a client can get revocations from since 31 days ago maximum.</li>
 * </ul>
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@ConfigurationProperties(prefix = "cassandra")
public class CassandraProperties {

    private String keyspace = "revocation";

    private String contactPoints;

    private String clusterName = "Cassandra";

    private int port = ProtocolOptions.DEFAULT_PORT;

    private ConsistencyLevel writeConsistencyLevel = EACH_QUORUM;

    private ConsistencyLevel readConsistencyLevel = ONE;

    private Optional<String> username = Optional.empty();

    private Optional<String> password = Optional.empty();

    // Maybe this maxTimeDelta should be derived from the bucket size in Cassandra...
    private int maxTimeDelta = (int) Duration.ofDays(31).getSeconds();

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public String getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(String contactPoints) {
        this.contactPoints = contactPoints;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ConsistencyLevel getWriteConsistencyLevel() {
        return writeConsistencyLevel;
    }

    public void setWriteConsistencyLevel(ConsistencyLevel writeConsistencyLevel) {
        this.writeConsistencyLevel = writeConsistencyLevel;
    }

    public ConsistencyLevel getReadConsistencyLevel() {
        return readConsistencyLevel;
    }

    public void setReadConsistencyLevel(ConsistencyLevel readConsistencyLevel) {
        this.readConsistencyLevel = readConsistencyLevel;
    }

    public Optional<String> getUsername() {
        return username;
    }

    public void setUsername(Optional<String> username) {
        this.username = username;
    }

    public Optional<String> getPassword() {
        return password;
    }

    public void setPassword(Optional<String> password) {
        this.password = password;
    }

    public int getMaxTimeDelta() {
        return maxTimeDelta;
    }

    public void setMaxTimeDelta(int maxTimeDelta) {
        this.maxTimeDelta = maxTimeDelta;
    }
}
