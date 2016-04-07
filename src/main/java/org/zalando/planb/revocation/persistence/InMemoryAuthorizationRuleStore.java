package org.zalando.planb.revocation.persistence;

import org.zalando.planb.revocation.domain.AuthorizationRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryAuthorizationRuleStore implements AuthorizationRulesStore.Internal {

    private List<AuthorizationRule> claims = new ArrayList<>();

    @Override
    public Collection<AuthorizationRule> withTargetClaims(AuthorizationRule authorizationRule) {
        return claims.stream().filter(authorizationRule::containsTargetClaims).collect(Collectors.toSet());
    }

   @Override
    public void storeAccessRule(AuthorizationRule authorizationRule) {
        claims.add(authorizationRule);
    }

    public void cleanup() {
        claims.clear();
    }
}
