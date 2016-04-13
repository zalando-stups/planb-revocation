package org.zalando.planb.revocation.config.properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.planb.revocation.config.RevocationConfig;
import org.zalando.planb.revocation.domain.RevocationType;

import java.util.EnumMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RevocationProperties}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RevocationConfig.class)
public class RevocationPropertiesTest {

    @Autowired
    private RevocationProperties properties;

    @Test
    public void testSetters() {
        int timestampThreshold = 10;

        properties.setTimestampThreshold(timestampThreshold);

        assertThat(properties.getTimestampThreshold()).isEqualTo(timestampThreshold);
    }
}