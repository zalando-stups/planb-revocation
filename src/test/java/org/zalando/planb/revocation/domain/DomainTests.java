package org.zalando.planb.revocation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit tests for domain objects.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class DomainTests {

    /**
     * Tests that when instantiating a {@link Refresh}, all default values are not {@code null}.
     */
    @Test
    public void testRefresh() {
        Refresh r = Refresh.builder().build();

        assertThat(r.getRefreshTimestamp()).isNotNull();
    }
}
