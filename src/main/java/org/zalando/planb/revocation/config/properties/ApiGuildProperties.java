package org.zalando.planb.revocation.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

/**
 * Contains properties used for configuring the application according to Zalando's API Guild.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@ConfigurationProperties(prefix = "api")
public class ApiGuildProperties {

    private String swaggerFile = "classpath:api/swagger.yml";

    public String getSwaggerFile() {
        return swaggerFile;
    }

    public void setSwaggerFile(String swaggerFile) {
        this.swaggerFile = Objects.requireNonNull(swaggerFile, "'api.swaggerFile' cannot be null");
    }
}