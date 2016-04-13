package org.zalando.planb.revocation.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.AddressTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.persistence.AuthorizationRulesStore;
import org.zalando.planb.revocation.persistence.CassandraAuthorizationRuleStore;
import org.zalando.planb.revocation.persistence.CassandraRevocationStore;
import org.zalando.planb.revocation.persistence.InMemoryAuthorizationRuleStore;
import org.zalando.planb.revocation.persistence.InMemoryRevocationStore;
import org.zalando.planb.revocation.persistence.RevocationStore;

import java.util.Optional;

@Configuration
@EnableConfigurationProperties(CassandraProperties.class)
public class StorageConfig {

    @Autowired
    private CassandraProperties cassandraProperties;

    @Autowired
    private Optional<AddressTranslator> addressTranslator;

    /**
     * Storage used for revocations.
     * <p>
     * <p>If no {@link CassandraProperties} are defined, defaults to in-memory storage.</p>
     *
     * @return the resulting {@code RevocationStore}
     * @see CassandraProperties
     */
    @Bean
    public RevocationStore revocationStore() {

        // Defaults to in-memory, when CassandraProperties are absent;
        if (StringUtils.isEmpty(cassandraProperties.getContactPoints())) {
            return new InMemoryRevocationStore();
        }

        return new CassandraRevocationStore(cassandraSession(), cassandraProperties.getReadConsistencyLevel(),
                cassandraProperties.getWriteConsistencyLevel(), cassandraProperties.getMaxTimeDelta());
    }

    @Bean
    public AuthorizationRulesStore authorizationRulesStore() {

        // Defaults to in-memory, when CassandraProperties are absent;
        if (StringUtils.isEmpty(cassandraProperties.getContactPoints())) {
            return new InMemoryAuthorizationRuleStore();
        }

        return new CassandraAuthorizationRuleStore(cassandraSession(), cassandraProperties.getReadConsistencyLevel(),
                cassandraProperties.getWriteConsistencyLevel());
    }

    public Session cassandraSession() {

        // Build Cluster
        final Cluster.Builder builder = Cluster.builder();
        builder.addContactPoints(cassandraProperties.getContactPoints().split(","));
        addressTranslator.ifPresent(builder::withAddressTranslator);
        builder.withClusterName(cassandraProperties.getClusterName());
        builder.withPort(cassandraProperties.getPort());

        // Only set credentials if they exist
        if (cassandraProperties.getUsername().isPresent() && cassandraProperties.getPassword().isPresent()) {
            builder.withCredentials(cassandraProperties.getUsername().get(), cassandraProperties.getPassword().get());
        }

        return builder.build().connect(cassandraProperties.getKeyspace());
    }
}
