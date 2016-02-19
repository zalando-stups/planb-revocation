package org.zalando.planb.revocation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.zalando.planb.revocation.config.properties.ApiGuildProperties;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.config.properties.MessageDigestProperties;
import org.zalando.planb.revocation.persistence.CassandraStorage;
import org.zalando.planb.revocation.persistence.InMemoryStore;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.service.SwaggerService;
import org.zalando.planb.revocation.service.impl.SwaggerFromYamlFileService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import lombok.Getter;
import org.zalando.planb.revocation.util.MessageHasher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableConfigurationProperties({CassandraProperties.class, ApiGuildProperties.class, MessageDigestProperties.class})
@Getter
public class PlanBRevocationConfig {
//    private List<String> cassandraSeedNodes;
//
//    private List<MessageDigestConfig> saltList;

    @Autowired
    private CassandraProperties cassandraProperties;

    @Autowired
    private MessageDigestProperties messageDigestProperties;

    @Autowired
    private ApiGuildProperties apiGuildProperties;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    }

    @Bean
    public MessageHasher messageDigest() throws NoSuchAlgorithmException {
        if(messageDigestProperties.getAlgorithm() == null) return null;

        // TODO vou aqui
        MessageDigest messageDigest = MessageDigest.getInstance(messageDigestProperties.getAlgorithm());
        return new MessageHasher(messageDigestProperties.getAlgorithm(), messageDigestProperties.getSaltFile());
    }

    @Bean
    public RevocationStore revocationStore() {
        if (StringUtils.isEmpty(cassandraProperties.getContactPoints())) {
            return new InMemoryStore();
        }

        return new CassandraStorage(cassandraProperties);
    }

    @Bean
    public SwaggerService swaggerService() {
        return new SwaggerFromYamlFileService(apiGuildProperties.getSwaggerPath());
    }
}
