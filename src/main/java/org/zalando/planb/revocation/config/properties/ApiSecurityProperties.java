package org.zalando.planb.revocation.config.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "api.security")
@Getter
@Setter
public class ApiSecurityProperties {

    private Map<String, String> oauth2Scopes = new HashMap<String, String>(0);

}
