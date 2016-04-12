package org.zalando.planb.revocation.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

/**
 * Created by rreis on 17/02/16.
 */
@ConfigurationProperties(prefix = "api.security")
@Data
public class ApiSecurityProperties {

    private String revokeExpr;
}
