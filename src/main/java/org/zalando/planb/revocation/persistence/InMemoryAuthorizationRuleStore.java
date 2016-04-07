package org.zalando.planb.revocation.persistence;

import org.springframework.stereotype.Component;
import org.zalando.planb.revocation.domain.AuthorizationRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryAuthorizationRuleStore implements AuthorizationRulesStore {

    private List<AuthorizationRule> claims = new ArrayList<>();

    @Override
    public Collection<AuthorizationRule> withTargetClaims(AuthorizationRule authorizationRule) {
        return claims.stream().filter(authorizationRule::containsTargetClaims).collect(Collectors.toSet());
    }

    @Override
    public Collection<AuthorizationRule> withSourceClaims(AuthorizationRule authorizationRule) {
        return claims.stream().filter(authorizationRule::containsSourceClaims).collect(Collectors.toSet());
    }

    @Override
    public void storeAccessRule(AuthorizationRule authorizationRule) {
        claims.add(authorizationRule);
    }
}
