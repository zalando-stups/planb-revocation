package org.zalando.planb.revocation.config.properties;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.planb.revocation.AbstractSpringTest;
import org.zalando.planb.revocation.Main;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ApiSecurityProperties}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("test")
public class ApiSecurityPropertiesTest extends AbstractSpringTest {

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