package org.zalando.planb.revocation.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * General configuration properties used for posting revocations.
 *
 * <p>The following properties are used and can be defined through <a
 * href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html">Spring
 * Configuration</a>:</p>
 *
 * <ul>
 *   <li>{@code revocation.timestampThreshold} - A value in seconds used to give a threshold for {@code issued_before}
 *   values when posting revocations. Default value is 5 seconds. This means that a revocation will be accepted if
 *   {@code issued_before} is equal to the current
 * </ul>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@ConfigurationProperties(prefix = "revocation")
@Data
public class RevocationProperties {

    private Integer timestampThreshold = 5;
}
