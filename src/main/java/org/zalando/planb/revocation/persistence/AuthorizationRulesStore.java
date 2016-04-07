package org.zalando.planb.revocation.persistence;

import org.zalando.planb.revocation.domain.AuthorizationRule;

import java.util.Collection;

public interface AuthorizationRulesStore {

    Collection<AuthorizationRule> withTargetClaims(AuthorizationRule authorizationRule);

    void storeAccessRule(AuthorizationRule authorizationRule);

    /**
     * Only intended for internal use, do not implement
     */
    interface Internal extends AuthorizationRulesStore {

        void cleanup();

    }
}
