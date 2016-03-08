package org.zalando.planb.revocation.config;

import java.util.EnumMap;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.util.StringUtils;

import org.zalando.planb.revocation.config.properties.ApiGuildProperties;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.domain.RevocationFlag;
import org.zalando.planb.revocation.persistence.CassandraStorage;
import org.zalando.planb.revocation.persistence.InMemoryStore;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.service.SchemaDiscoveryService;
import org.zalando.planb.revocation.service.SwaggerService;
import org.zalando.planb.revocation.service.impl.StaticSchemaDiscoveryService;
import org.zalando.planb.revocation.service.impl.SwaggerFromYamlFileService;

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
     * Meta information included when a client get revocations.
     *
     * @return  an {@link EnumMap} indexed by {@link RevocationFlag}
     */
    @Bean
    public EnumMap<RevocationFlag, Object> metaInformation() {
        return new EnumMap<>(RevocationFlag.class);
    }

    /**
     * Storage used for revocations.
     *
     * <p>If no {@link CassandraProperties} are defined, defaults to in-memory storage.</p>
     *
     * @param   metaInformation  if the store is a Cassandra cluster, adds the {@code cassandra.maxTimeDelta} as in the
     *                           meta information.
     *
     * @return  the resulting {@code RevocationStore}
     *
     * @see     CassandraProperties
     */
    @Bean
    public RevocationStore revocationStore(final EnumMap<RevocationFlag, Object> metaInformation) {
        if (StringUtils.isEmpty(cassandraProperties.getContactPoints())) {
            return new InMemoryStore();
        }

        metaInformation.put(RevocationFlag.MAX_TIME_DELTA, cassandraProperties.getMaxTimeDelta());
        return new CassandraStorage(cassandraProperties);
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
