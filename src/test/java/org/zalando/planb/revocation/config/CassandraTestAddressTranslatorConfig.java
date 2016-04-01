package org.zalando.planb.revocation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.policies.AddressTranslator;
import com.datastax.driver.core.policies.IdentityTranslator;

@Configuration
public class CassandraTestAddressTranslatorConfig {

    @Bean
    AddressTranslator addressTranslator() {
        return new IdentityTranslator();
    }
}
