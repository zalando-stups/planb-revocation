package org.zalando.planb.revocation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.config.properties.HashingProperties;
import org.zalando.planb.revocation.config.properties.RevocationProperties;
import org.zalando.planb.revocation.persistence.AuthorizationRulesStore;
import org.zalando.planb.revocation.service.RevocationAuthorizationService;
import org.zalando.planb.revocation.service.impl.RuleBasedClaimRevocationAuthorizationService;
import org.zalando.planb.revocation.util.ImmutableMessageHasher;
import org.zalando.planb.revocation.util.MessageHasher;

@Configuration
@EnableConfigurationProperties({HashingProperties.class, RevocationProperties.class})
public class RevocationConfig {

    @Autowired
    private HashingProperties hashingProperties;

    @Bean
    public MessageHasher messageHasher() {
        return ImmutableMessageHasher.builder()
                .hashingAlgorithms(hashingProperties.getAlgorithms())
                .salt(hashingProperties.getSalt())
                .separator(hashingProperties.getSeparator())
                .build();
    }

    @Bean
    public RevocationAuthorizationService revocationAuthorizationService(
            AuthorizationRulesStore authorizationRulesStore,
            RevocationProperties revocationProperties,
            CassandraProperties cassandraProperties) {
        return new RuleBasedClaimRevocationAuthorizationService(authorizationRulesStore, revocationProperties, cassandraProperties);
    }
}
