package org.zalando.planb.revocation.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.util.StringUtils;
import org.zalando.planb.revocation.config.properties.ApiGuildProperties;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.persistence.CassandraStorage;
import org.zalando.planb.revocation.persistence.InMemoryStore;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.service.SwaggerService;
import org.zalando.planb.revocation.service.impl.SwaggerFromYamlFileService;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import lombok.Getter;

@Configuration
@EnableConfigurationProperties({ CassandraProperties.class, ApiGuildProperties.class })
@Getter
public class PlanBRevocationConfig {
    private List<String> cassandraSeedNodes;

    private List<SaltConfig> saltList;

    private String oauth2TokenInfoUrl;

    private Map<String, String> oauth2Scopes = new HashMap<>(0);

    @Autowired
    private CassandraProperties cassandraProperties;

    @Autowired
    private ApiGuildProperties apiGuildProperties;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        return om;
    }

    @Bean
    public RevocationStore revocationStore() {
        if(StringUtils.isEmpty(cassandraProperties.getContactPoints())) {
            return new InMemoryStore();
        }

        return new CassandraStorage(cassandraProperties);
    }

    @Bean
    public SwaggerService swaggerService() {
        return new SwaggerFromYamlFileService(apiGuildProperties.getSwagger());
    }
}
