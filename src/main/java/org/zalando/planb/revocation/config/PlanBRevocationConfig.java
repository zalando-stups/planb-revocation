package org.zalando.planb.revocation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.planb.revocation.config.properties.ApiGuildProperties;
import org.zalando.planb.revocation.service.SchemaDiscoveryService;
import org.zalando.planb.revocation.service.SwaggerService;
import org.zalando.planb.revocation.service.impl.StaticSchemaDiscoveryService;
import org.zalando.planb.revocation.service.impl.SwaggerFromYamlFileService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

@Configuration
@EnableConfigurationProperties(ApiGuildProperties.class)
public class PlanBRevocationConfig {

    @Autowired
    private ApiGuildProperties apiGuildProperties;

    @Bean
    public SwaggerService swaggerService(ApplicationContext context) {
        return new SwaggerFromYamlFileService(apiGuildProperties.getSwaggerFile());
    }

    @Bean
    public SchemaDiscoveryService schemaDiscoveryService() {
        return new StaticSchemaDiscoveryService();
    }
}
