package org.zalando.planb.revocation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.datastax.driver.core.policies.AddressTranslator;
import com.datastax.driver.core.policies.EC2MultiRegionAddressTranslator;

@Configuration
@Profile("aws")
public class AwsConfig {

    @Bean
    public AddressTranslator addressTranslator() {
        return new EC2MultiRegionAddressTranslator();
    }

}
