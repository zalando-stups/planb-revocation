package org.zalando.planb.revocation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.util.persistence.CassandraSupportStore;

/**
 * Support beans for accessing a Cassandra data store.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Configuration
@EnableConfigurationProperties(CassandraProperties.class)
@Profile("it")
public class SupportCassandraConfig {

    @Autowired
    private CassandraProperties cassandraProperties;

    @Autowired
    private StorageConfig storageConfig;

    @Bean
    public CassandraSupportStore auditStore() {
        return new CassandraSupportStore(storageConfig.cassandraSession(),
                cassandraProperties.getReadConsistencyLevel(), cassandraProperties.getWriteConsistencyLevel(),
                cassandraProperties.getMaxTimeDelta());
    }
}
