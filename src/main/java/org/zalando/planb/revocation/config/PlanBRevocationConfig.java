package org.zalando.planb.revocation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.planb.revocation.persistence.InMemoryStore;
import org.zalando.planb.revocation.persistence.RevocationStore;

import java.util.List;

@Configuration
@Getter
public class PlanBRevocationConfig {
    List<String> cassandraSeedNodes;
    List<SaltConfig> saltList;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        return om;
    }

    @Bean
    public RevocationStore revocationStore() {
         return new InMemoryStore();
    }
}
