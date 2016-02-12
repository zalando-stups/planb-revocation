package org.zalando.planb.revocation.persistence;

import org.springframework.context.annotation.Bean;

import org.zalando.planb.revocation.config.PlanBRevocationConfig;

/**
 * Created by jmussler on 12.02.16.
 */
public class PersistenceFactory {

    @Bean
    public RevocationStore getRevocationStore(final PlanBRevocationConfig config) {
        if (config.getCassandraSeedNodes().isEmpty()) {
            return new InMemoryStore();
        }

        // TODO @jmussler fix this!
        return null;
    }
}
