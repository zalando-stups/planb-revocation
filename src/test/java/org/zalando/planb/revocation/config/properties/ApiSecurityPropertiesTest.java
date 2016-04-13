package org.zalando.planb.revocation.config.properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.planb.revocation.config.PlanBRevocationConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ApiSecurityProperties}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PlanBRevocationConfig.class)
public class ApiSecurityPropertiesTest {

    @Autowired
    private ApiSecurityProperties properties;

    @Test(expected = NullPointerException.class)
    public void testNullPointerExceptionWhenNullValue() {
        properties.setRevokeExpr(null);
    }

    @Test
    public void testSetters() {
        String expected = "#oauth2.hasScope('uid')";
        properties.setRevokeExpr(expected);

        assertThat(properties.getRevokeExpr()).isEqualTo(expected);
    }
}