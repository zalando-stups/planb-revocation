package org.zalando.planb.revocation.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for domain objects.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class DomainTests {

    private static final int ONE_HOUR_BEFORE = 1457565076;

    private static final int ONE_MINUTE_BEFORE = 1457568706;

    /**
     * Tests that a default timestamp is set in {@link RevokedClaimsData#issuedBefore} when creating a new instance.
     */
    @Test
    public void testIssuedBeforeDefaultTimestampInClaim() {
        RevokedClaimsData claimsData = new RevokedClaimsData();

        assertThat(claimsData.getIssuedBefore()).isNotNull();
    }
}
