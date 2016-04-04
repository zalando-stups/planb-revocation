package org.zalando.planb.revocation.config.properties;

import static com.datastax.driver.core.ConsistencyLevel.EACH_QUORUM;
import static com.datastax.driver.core.ConsistencyLevel.ONE;

import java.time.Duration;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ProtocolOptions;

import lombok.Data;

/**
 * Properties used to configure a Cassandra cluster data source.
 *
 * <p>The following properties are used and can be defined through <a
 * href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html">Spring
 * Configuration</a>:</p>
 *
 * <ul>
 *   <li>{@code cassandra.keyspace} - Keyspace in the Cassandra cluster. Default value is {@code revocation} (See the
 *     provided <a href="https://github.com/zalando/planb-revocation/blob/master/revocation_schema.cql">CQL
 *     Script</a>);</li>
 *   <li>{@code cassandra.contactPoints} - Comma separated list of hosts;</li>
 *   <li>{@code cassandra.clusterName} - Name of the Cassandra cluster. Default value is {@code Cassandra};</li>
 *   <li>{@code cassandra.port} - Port of the Cassandra cluster. Default value is {@code 9042};</li>
 *   <li>{@code cassandra.writeConsistencyLevel} - Consistency level for write operations. Default value is {@code ONE};
 *   </li>
 *   <li>{@code cassandra.readConsistencyLevel} - Consistency level for read operations. Default value is
 *     {@code EACH_QUORUM};</li>
 *   <li>{@code cassandra.username} - User account to access the Cassandra cluster. Default value is empty;</li>
 *   <li>{@code cassandra.password} - User password to access the Cassandra cluster. Default value is empty;</li>
 *   <li>{@code cassandra.maxTimeDelta} - The maximum time span limit to get revocations, in seconds. Default value is
 *     {@code 2592000}, meaning that a client can get revocations from since 30 days ago maximum.</li>
 * </ul>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@ConfigurationProperties(prefix = "cassandra")
@Data
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
    private int maxTimeDelta = (int) Duration.ofDays(30).getSeconds();
}
