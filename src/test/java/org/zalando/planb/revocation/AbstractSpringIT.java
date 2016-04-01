package org.zalando.planb.revocation;

import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.planb.revocation.util.persistence.CassandraSupportStore;

/**
 * Abstract class for Integration Tests that cleans up store.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@ActiveProfiles("it")
public abstract class AbstractSpringIT extends AbstractSpringTest {

    @Autowired
    private CassandraSupportStore auditStore;

    /**
     * Cleans up Cassandra revocation keyspace between tests.
     *
     * <p>Needed because all tests are written assuming an empty store.</p>
     */
    @After
    public void cleanup() {

        auditStore.cleanup();
    }
}
