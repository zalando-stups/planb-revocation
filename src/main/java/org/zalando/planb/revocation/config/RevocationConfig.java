package org.zalando.planb.revocation.config;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.zalando.planb.revocation.config.properties.HashingProperties;
import org.zalando.planb.revocation.config.properties.RevocationProperties;
import org.zalando.planb.revocation.util.MessageHasher;

import lombok.Getter;

@Configuration
@EnableConfigurationProperties({HashingProperties.class, RevocationProperties.class})
@Getter
public class RevocationConfig {

    @Autowired
    private HashingProperties hashingProperties;

    @Bean
    public MessageHasher messageHasher() throws NoSuchAlgorithmException {
        return new MessageHasher(hashingProperties.getAlgorithms(), hashingProperties.getSalt(),
                hashingProperties.getSeparator());
    }
}
