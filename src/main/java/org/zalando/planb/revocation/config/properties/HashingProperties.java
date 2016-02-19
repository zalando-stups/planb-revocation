package org.zalando.planb.revocation.config.properties;

import java.util.EnumMap;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.zalando.planb.revocation.domain.RevocationType;

import lombok.Data;

/**
 * Created by rreis on 2/18/16.
 */
@ConfigurationProperties(prefix = "revocation.hashing")
@Data
public class HashingProperties {

    EnumMap<RevocationType, String> algorithms;

    String salt;
}
