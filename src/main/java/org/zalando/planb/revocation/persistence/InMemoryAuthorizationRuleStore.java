package org.zalando.planb.revocation.persistence;

import org.zalando.planb.revocation.domain.AuthorizationRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InMemoryAuthorizationRuleStore implements AuthorizationRulesStore.Internal {

    private List<AuthorizationRule> claims = new ArrayList<>();

    @Override
    public Collection<AuthorizationRule> retrieveByMatchingAllowedClaims(AuthorizationRule authorizationRule) {
        return findMatchingRulesByAllowedClaims(claims, authorizationRule);
    }

   @Override
    public void store(AuthorizationRule authorizationRule) {
        claims.add(authorizationRule);
    }

    public void cleanup() {
        claims.clear();
    }

}
