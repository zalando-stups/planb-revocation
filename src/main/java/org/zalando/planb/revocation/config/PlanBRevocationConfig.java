package org.zalando.planb.revocation.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.zalando.planb.revocation.persistence.CassandraStorage;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.service.SwaggerService;
import org.zalando.planb.revocation.service.impl.SwaggerFromYamlFileService;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import lombok.Getter;

@Configuration
@Getter
public class PlanBRevocationConfig {
    List<String> cassandraSeedNodes;
    List<SaltConfig> saltList;

    @Autowired
    CassandraProperties cassandraProperties;

    @Autowired
    ApiGuildProperties apiGuildProperties;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        return om;
    }

    @Bean
    public RevocationStore revocationStore() {
        return new CassandraStorage();
    }

    @Bean
    Session session() {
        Cluster cluster = Cluster.builder().addContactPoints(cassandraProperties.getContactPoints().split(","))
                                 .withClusterName(cassandraProperties.getClusterName())
                                 .withPort(cassandraProperties.getPort()).build();

        return cluster.connect(cassandraProperties.getKeyspace());
    }

    @Bean
    public SwaggerService swaggerService() {
        return new SwaggerFromYamlFileService(apiGuildProperties.getSwagger());
    }
}
