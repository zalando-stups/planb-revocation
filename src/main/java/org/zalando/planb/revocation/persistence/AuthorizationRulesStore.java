package org.zalando.planb.revocation.persistence;

import org.zalando.planb.revocation.domain.AuthorizationRule;

import java.util.Collection;

public interface AuthorizationRulesStore {

    Collection<AuthorizationRule> withTargetClaims(AuthorizationRule authorizationRule);

    Collection<AuthorizationRule> withSourceClaims(AuthorizationRule authorizationRule);

    void storeAccessRule(AuthorizationRule authorizationRule);

}
