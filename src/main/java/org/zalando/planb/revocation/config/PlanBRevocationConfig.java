package org.zalando.planb.revocation.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Data
public class PlanBRevocationConfig {
    List<String> cassandraSeedNodes;
    List<SaltConfig> saltList;
}
