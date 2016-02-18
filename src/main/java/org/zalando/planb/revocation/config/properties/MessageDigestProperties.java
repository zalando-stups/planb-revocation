package org.zalando.planb.revocation.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by rreis on 2/18/16.
 */
@ConfigurationProperties(prefix = "messageDigest")
@Data
public class MessageDigestProperties {

    String algorithm;

    String saltFile;
}
