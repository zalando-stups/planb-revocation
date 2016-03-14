package org.zalando.planb.revocation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

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
     * Tests that when instantiating a {@link Refresh} all values are set, including defaults.
     */
    @Test
    public void testDefaultRefresh() {

        Refresh r = Refresh.builder().refreshFrom(ONE_HOUR_BEFORE).build();
        assertThat(r.refreshFrom()).isEqualTo(ONE_HOUR_BEFORE);
        assertThat(r.refreshTimestamp()).isNotNull();
    }

    /**
     * Tests assigning a timestamp to a {@link Refresh} object.
     */
    @Test
    public void testRefreshWithTimestamp() {

        Refresh r = Refresh.builder().refreshFrom(ONE_HOUR_BEFORE).refreshTimestamp(ONE_MINUTE_BEFORE).build();
        assertThat(r.refreshTimestamp()).isEqualTo(ONE_MINUTE_BEFORE);
    }
}
