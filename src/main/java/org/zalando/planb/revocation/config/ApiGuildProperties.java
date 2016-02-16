package org.zalando.planb.revocation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Data
@ConfigurationProperties(prefix = "api")
public class ApiGuildProperties {

    private String swagger;
}
