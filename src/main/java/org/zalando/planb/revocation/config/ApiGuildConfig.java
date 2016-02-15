package org.zalando.planb.revocation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.zalando.planb.revocation.service.SwaggerService;
import org.zalando.planb.revocation.service.impl.SwaggerFromYamlFileService;

import lombok.Data;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "api")
public class ApiGuildConfig {

    private String swagger;

    @Bean
    public SwaggerService swaggerService() {
        return new SwaggerFromYamlFileService(swagger);
    }
}
