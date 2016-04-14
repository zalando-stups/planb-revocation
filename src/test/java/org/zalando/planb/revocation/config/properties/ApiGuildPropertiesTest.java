package org.zalando.planb.revocation.config.properties;

import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ApiGuildProperties}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class ApiGuildPropertiesTest {

    @Test(expected = NullPointerException.class)
    public void testNullPointerExceptionWhenNullValue() {
        ApiGuildProperties properties = new ApiGuildProperties();

        properties.setSwaggerFile(null);
    }

    @Test
    public void testSetters() {
        ApiGuildProperties properties = new ApiGuildProperties();

        String expected = "classpath:api/openapi.yaml";
        properties.setSwaggerFile(expected);

        assertThat(properties.getSwaggerFile()).isEqualTo(expected);
    }
}