package org.zalando.planb.revocation.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by rreis on 17/02/16.
 */
@ConfigurationProperties(prefix = "security.oauth2.resource")
@Data
public class SecurityProperties {

    private String tokenInfoUri;

    private String revoke;
}
