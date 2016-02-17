package org.zalando.planb.revocation.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Data
@ConfigurationProperties(prefix = "api.swagger")
public class ApiGuildProperties {

    private String swaggerPath;
}
