package org.zalando.planb.revocation.config.properties;

import org.junit.Test;
import org.zalando.planb.revocation.AbstractSpringTest;
import org.zalando.planb.revocation.domain.RevocationType;

import java.util.EnumMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link HashingProperties}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class HashingPropertiesTest {

    @Test
    public void testSetters() {
        HashingProperties properties = new HashingProperties();

        EnumMap<RevocationType, String> algorithms = new EnumMap<RevocationType, String>(RevocationType.class);
        algorithms.put(RevocationType.CLAIM, "MD5");
        algorithms.put(RevocationType.TOKEN, "MD5");

        String salt = "bckjabvcuzabcoiuwabcwu";
        Character separator = ':';

        properties.setAlgorithms(algorithms);
        properties.setSalt(salt);
        properties.setSeparator(separator);

        assertThat(properties.getAlgorithms()).isEqualTo(algorithms);
        assertThat(properties.getSalt()).isEqualTo(salt);
        assertThat(properties.getSeparator()).isEqualTo(separator);
    }
}
