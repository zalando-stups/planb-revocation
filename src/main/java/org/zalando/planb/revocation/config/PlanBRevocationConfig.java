package org.zalando.planb.revocation.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.planb.revocation.persistence.InMemoryStore;
import org.zalando.planb.revocation.persistence.RevocationStore;

import java.util.List;

@Configuration
@Data
public class PlanBRevocationConfig {
    List<String> cassandraSeedNodes;
    List<SaltConfig> saltList;

    @Bean
    public RevocationStore revocationStore() {
         return new InMemoryStore();
    }
}
