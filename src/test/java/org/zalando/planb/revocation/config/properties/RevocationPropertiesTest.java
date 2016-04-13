package org.zalando.planb.revocation.config.properties;

import org.junit.Test;
import org.zalando.planb.revocation.AbstractSpringTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RevocationProperties}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class RevocationPropertiesTest extends AbstractSpringTest {

    @Test
    public void testSetters() {
        RevocationProperties properties = new RevocationProperties();

        int timestampThreshold = 10;

        properties.setTimestampThreshold(timestampThreshold);

        assertThat(properties.getTimestampThreshold()).isEqualTo(timestampThreshold);
    }
}