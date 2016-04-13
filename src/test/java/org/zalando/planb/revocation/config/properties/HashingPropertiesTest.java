package org.zalando.planb.revocation.config.properties;

import com.datastax.driver.core.ConsistencyLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.planb.revocation.config.RevocationConfig;
import org.zalando.planb.revocation.config.StorageConfig;
import org.zalando.planb.revocation.domain.RevocationType;

import java.util.EnumMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link HashingProperties}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RevocationConfig.class)
public class HashingPropertiesTest {

    @Autowired
    private HashingProperties properties;

    @Test
    public void testSetters() {
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