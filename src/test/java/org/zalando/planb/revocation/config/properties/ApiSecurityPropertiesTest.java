package org.zalando.planb.revocation.config.properties;

import org.junit.Test;
import org.zalando.planb.revocation.AbstractSpringTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ApiSecurityProperties}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class ApiSecurityPropertiesTest extends AbstractSpringTest {

    @Test(expected = NullPointerException.class)
    public void testNullPointerExceptionWhenNullValue() {
        ApiSecurityProperties properties = new ApiSecurityProperties();

        properties.setRevokeExpr(null);
    }

    @Test
    public void testSetters() {
        ApiSecurityProperties properties = new ApiSecurityProperties();

        String expected = "#oauth2.hasScope('uid')";
        properties.setRevokeExpr(expected);

        assertThat(properties.getRevokeExpr()).isEqualTo(expected);
    }
}