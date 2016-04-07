package org.zalando.planb.revocation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.zalando.planb.revocation.util.UnixTimestamp;
import org.zalando.planb.revocation.util.security.WithMockCustomUser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Unit tests for domain objects.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
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

    /**
     * Tests that a default timestamp is set in {@link RevokedTokenData#issuedBefore} when creating a new instance.
     */
    @Test
    public void testIssuedBeforeDefaultTimestampInToken() {
        RevokedTokenData claimsData = new RevokedTokenData();

        assertThat(claimsData.getIssuedBefore()).isNotNull();
    }
}
