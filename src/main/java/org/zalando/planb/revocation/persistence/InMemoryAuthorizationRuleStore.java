package org.zalando.planb.revocation.persistence;

import org.springframework.stereotype.Component;
import org.zalando.planb.revocation.domain.AuthorizationRule;

import java.util.Collection;
import java.util.Collections;

@Component
public class InMemoryAuthorizationRuleStore implements AuthorizationRulesStore {

    @Override
    public Collection<AuthorizationRule> getAccessList(AuthorizationRule authorizationRule) {
        return Collections.emptyList();
    }

    @Override
    public boolean storeAccessRule(AuthorizationRule authorizationRule) {
        return false;
    }
}
