package org.zalando.planb.revocation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.planb.revocation.config.properties.HashingProperties;
import org.zalando.planb.revocation.config.properties.RevocationProperties;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.util.ImmutableMessageHasher;
import org.zalando.planb.revocation.util.MessageHasher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({HashingProperties.class, RevocationProperties.class})
public class RevocationConfig {

    @Autowired
    private HashingProperties hashingProperties;

    @Bean
    public MessageHasher messageHasher() throws NoSuchAlgorithmException {
        Map<RevocationType, MessageDigest> hashers = new HashMap<>(hashingProperties.getAlgorithms().size());
        for (RevocationType type : hashingProperties.getAlgorithms().keySet()) {
            hashers.put(type, MessageDigest.getInstance(hashingProperties.getAlgorithms().get(type)));
        }

        return ImmutableMessageHasher.builder()
                .hashingAlgorithms(hashers)
                .salt(hashingProperties.getSalt())
                .separator(hashingProperties.getSeparator())
                .build();
    }
}
