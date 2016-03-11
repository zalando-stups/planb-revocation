package org.zalando.planb.revocation.config;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.util.StringUtils;

import org.zalando.planb.revocation.config.properties.ApiGuildProperties;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.persistence.CassandraStore;
import org.zalando.planb.revocation.persistence.InMemoryStore;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.service.SchemaDiscoveryService;
import org.zalando.planb.revocation.service.SwaggerService;
import org.zalando.planb.revocation.service.impl.StaticSchemaDiscoveryService;
import org.zalando.planb.revocation.service.impl.SwaggerFromYamlFileService;

import com.datastax.driver.core.Cluster;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import lombok.Getter;

@Configuration
@EnableConfigurationProperties({ CassandraProperties.class, ApiGuildProperties.class })
@Getter
public class PlanBRevocationConfig {

    @Autowired
    private CassandraProperties cassandraProperties;

    @Autowired
    private ApiGuildProperties apiGuildProperties;

    @Bean
    public ObjectMapper objectMapper() {
        return
            new ObjectMapper().setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    }

    /**
     * Storage used for revocations.
     *
     * <p>If no {@link CassandraProperties} are defined, defaults to in-memory storage.</p>
     *
     * @return  the resulting {@code RevocationStore}
     *
     * @see     CassandraProperties
     */
    @Bean
    public RevocationStore revocationStore() {

        // Defaults to in-memory, when CassandraProperties are absent;
        if (StringUtils.isEmpty(cassandraProperties.getContactPoints())) {
            return new InMemoryStore();
        }

        // Build Cluster
        final Cluster.Builder builder = Cluster.builder();
        builder.addContactPoints(cassandraProperties.getContactPoints().split(","));
        builder.withClusterName(cassandraProperties.getClusterName());
        builder.withPort(cassandraProperties.getPort());

        // Only set credentials if they exist
        if (cassandraProperties.getUsername().isPresent() && cassandraProperties.getPassword().isPresent()) {
            builder.withCredentials(cassandraProperties.getUsername().get(), cassandraProperties.getPassword().get());
        }

        return new CassandraStore(builder.build().connect(cassandraProperties.getKeyspace()),
                cassandraProperties.getReadConsistencyLevel(), cassandraProperties.getWriteConsistencyLevel(),
                cassandraProperties.getMaxTimeDelta());
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
