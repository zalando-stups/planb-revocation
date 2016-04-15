package org.zalando.planb.revocation.config;

import com.datastax.driver.core.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.management.CassandraHealthIndicator;
import org.zalando.planb.revocation.persistence.AuthorizationRulesStore;
import org.zalando.planb.revocation.persistence.CassandraAuthorizationRuleStore;
import org.zalando.planb.revocation.persistence.CassandraRevocationStore;
import org.zalando.planb.revocation.persistence.InMemoryAuthorizationRuleStore;
import org.zalando.planb.revocation.persistence.InMemoryRevocationStore;
import org.zalando.planb.revocation.persistence.RevocationStore;

@Configuration
@AutoConfigureAfter(CassandraConfig.class)
public class StorageConfig {

    @Configuration
    @ConditionalOnBean(Session.class)
    public static class CassandraStorageConfig {

        @Autowired
        private CassandraProperties cassandraProperties;

        @Autowired
        private Session session;

        @Bean
        public RevocationStore revocationStore() {
            return new CassandraRevocationStore(session, cassandraProperties.getReadConsistencyLevel(),
                    cassandraProperties.getWriteConsistencyLevel(), cassandraProperties.getMaxTimeDelta());
        }

        @Bean
        public AuthorizationRulesStore authorizationRulesStore() {
            return new CassandraAuthorizationRuleStore(session, cassandraProperties.getReadConsistencyLevel(),
                    cassandraProperties.getWriteConsistencyLevel());
        }

        @Bean
        public CassandraHealthIndicator cassandraHealthIndicator() {
            return new CassandraHealthIndicator(session, cassandraProperties);
        }
    }

    @Configuration
    @ConditionalOnMissingBean(Session.class)
    public static class InMemoryStorageConfig {

        @Bean
        public RevocationStore revocationStore() {
            return new InMemoryRevocationStore();
        }

        @Bean
        public AuthorizationRulesStore authorizationRulesStore() {
            return new InMemoryAuthorizationRuleStore();
        }
    }

}
