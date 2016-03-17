package org.zalando.planb.revocation.config.properties;

import java.util.EnumMap;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.RevokedClaimsInfo;
import org.zalando.planb.revocation.domain.RevokedTokenInfo;

import lombok.Data;

/**
 * Properties used to configure Hashing of revocation values.
 *
 * <p>The following properties are used and can be defined through <a
 * href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html">Spring
 * Configuration</a>:</p>
 *
 * <ul>
 *   <li>{@code revocation.hashing.algorithms} - Algorithms used to hash values in {@link RevokedClaimsInfo} and
 *   {@link RevokedTokenInfo}. Default for all is {@code SHA-256}</li>;
 *   <li>{@code revocation.hashing.salt} - Salt value used to hash revocation values;</li>
 *   <li>{@code revocation.hashing.separator} - The separator used to concatenate claim values in
 *       {@link RevokedClaimsInfo}. Default is '|';</li>
 * </ul>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@ConfigurationProperties(prefix = "revocation.hashing")
@Data
public class HashingProperties {

    EnumMap<RevocationType, String> algorithms;

    String salt;

    Character separator = Character.valueOf('|');
}
