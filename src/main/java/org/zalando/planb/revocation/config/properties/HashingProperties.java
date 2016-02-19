package org.zalando.planb.revocation.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Created by rreis on 2/18/16.
 */
@ConfigurationProperties(prefix = "revocation.hashing")
@Data
public class HashingProperties {

    Map<String, String> algorithms;

    String salt;
}
