package org.zalando.planb.revocation.persistence;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.util.InstantTimestamp;
import org.zalando.planb.revocation.util.persistence.CassandraAuditStore;
import org.zalando.planb.revocation.util.security.WithMockCustomUser;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Implementation class for Cassandra store tests.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@ActiveProfiles("it")
public class CassandraStoreIT extends AbstractStoreTests {

    @Autowired
    private CassandraAuditStore auditStore;

    /**
     * Tests that audit information is written when inserting a refresh notification.
     */
    @Test
    @WithMockCustomUser
    public void testCreatedBySetWhenInsertRefresh() {

        // Insert refresh
        revocationStore.storeRefresh(InstantTimestamp.ONE_HOUR_AGO.seconds());

        // Get from store
        String createdBy = auditStore.getCreatedBy();

        // verify it's the default value for uid and realm from our mock user
        assertThat(createdBy).isEqualTo("/services/test0");
    }

    /**
     * Tests that audit information is written when inserting a revocation.
     */
    @Test
    @WithMockCustomUser
    public void testRevokedBySetWhenInsertRevocation() {

        // Insert revocation
        revocationStore.storeRevocation(generateRevocation(RevocationType.CLAIM));

        // Get from store
        Collection<String> revokedByValues = auditStore.getRevokedBy(InstantTimestamp.FIVE_MINUTES_AGO.seconds());

        // verify it's the default value for uid and realm from our mock user
        assertThat(revokedByValues.size()).isEqualTo(1);
        assertThat(revokedByValues.iterator().next()).isEqualTo("/services/test0");
    }
}
