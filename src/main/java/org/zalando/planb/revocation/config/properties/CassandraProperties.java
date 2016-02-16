package org.zalando.planb.revocation.config.properties;

import static com.datastax.driver.core.ConsistencyLevel.ONE;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ProtocolOptions;

import lombok.Data;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@ConfigurationProperties(prefix = "cassandra")
@Data
public class CassandraProperties {

    private String keyspace;

    private String contactPoints;

    private String clusterName;

    private Integer port = ProtocolOptions.DEFAULT_PORT;

    private ConsistencyLevel writeConsistencyLevel = ONE;

    private ConsistencyLevel readConsistencyLevel = ONE;

}
