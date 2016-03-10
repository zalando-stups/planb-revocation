package org.zalando.planb.revocation.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;

import org.springframework.test.context.ActiveProfiles;

import org.zalando.planb.revocation.AbstractSpringTest;
import org.zalando.planb.revocation.Main;
import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.util.InstantTimestamp;

/**
 * Abstract implementation of tests to be executed by multiple Spring Profiles.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
public abstract class AbstractStoreTests extends AbstractSpringTest {

    @Autowired
    private RevocationStore revocationStore;

    /**
     * Tests insertion of a refresh notification.
     *
     * <p>When retrieving the latest refresh, asserts that:
     *
     * <ul>
     *   <li>the retrieved {@code refreshFrom} value matches what was inserted;</li>
     *   <li>the retrieved {@code refreshTimestamp} value is greater than {@code refreshFrom};</li>
     * </ul>
     * </p>
     */
    @Test
    public void testInsertRefresh() {

        // Insert refresh
        revocationStore.storeRefresh(InstantTimestamp.ONE_HOUR_AGO.seconds());

        // Get from store
        Refresh fromStore = revocationStore.getRefresh();

        // verify it's the same
        assertThat(fromStore.refreshFrom()).isEqualTo(InstantTimestamp.ONE_HOUR_AGO.seconds());
        assertThat(fromStore.refreshTimestamp()).isGreaterThan(InstantTimestamp.ONE_HOUR_AGO.seconds());
    }
}
