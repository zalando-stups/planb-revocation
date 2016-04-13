package org.zalando.planb.revocation.config.properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.planb.revocation.config.PlanBRevocationConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ApiGuildProperties}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PlanBRevocationConfig.class)
public class ApiGuildPropertiesTest {

    @Autowired
    private ApiGuildProperties properties;

    @Test(expected = NullPointerException.class)
    public void testNullPointerExceptionWhenNullValue() {
        properties.setSwaggerFile(null);
    }

    @Test
    public void testSetSwaggerFile() {
        String expected = "classpath:api/openapi.yaml";
        properties.setSwaggerFile(expected);

        assertThat(properties.getSwaggerFile()).isEqualTo(expected);
    }
}