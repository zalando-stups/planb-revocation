package org.zalando.planb.revocation.api;


import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.planb.revocation.util.persistence.CassandraSupportStore;

@ActiveProfiles("it")
public class AuthorizationRulesIT extends AbstractAuthorizationRuleTest {

    @Autowired
    private CassandraSupportStore auditStore;

    @After
    public void cleanup() {
        auditStore.cleanup();
    }
}
