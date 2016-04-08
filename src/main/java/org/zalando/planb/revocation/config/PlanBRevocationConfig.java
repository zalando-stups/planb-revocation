package org.zalando.planb.revocation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.planb.revocation.config.properties.ApiGuildProperties;
import org.zalando.planb.revocation.service.SchemaDiscoveryService;
import org.zalando.planb.revocation.service.SwaggerService;
import org.zalando.planb.revocation.service.impl.StaticSchemaDiscoveryService;
import org.zalando.planb.revocation.service.impl.SwaggerFromYamlFileService;

@Configuration
@EnableConfigurationProperties(ApiGuildProperties.class)
@Getter
public class PlanBRevocationConfig {

    @Autowired
    private ApiGuildProperties apiGuildProperties;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new GuavaModule()).setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    }

    @Bean
    public SwaggerService swaggerService() {
        return new SwaggerFromYamlFileService(apiGuildProperties.getSwaggerFile());
    }

    @Bean
    public SchemaDiscoveryService schemaDiscoveryService() {
        return new StaticSchemaDiscoveryService();
    }
}
