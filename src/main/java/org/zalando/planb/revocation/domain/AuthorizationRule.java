package org.zalando.planb.revocation.domain;


import org.immutables.value.Value;

import java.util.Map;

/**
 * Represents an access rule in the context of claim-based token revocation.
 * An {@link AuthorizationRule rule} is defined by an arbitrary number of
 * {@link AuthorizationRule#requiredUserClaims() required user claims} a user
 * must satisfy in order to get authorization to perform revocations according to
 * an arbitrary number of {@link AuthorizationRule#allowedRevocationClaims() allowed revocation claims}
 */
@Value.Immutable
public abstract class AuthorizationRule {

    /**
     * Returns a map of claim names to claim values that represent required claims
     * a client must satisfy in order to have authorization to perform claim-based
     * revocation of tokens that match the entries contained
     * in {@link AuthorizationRule#allowedRevocationClaims()}
     *
     * @return required claims of the user to authorize token revocation.
     */
    public abstract Map<String, String> requiredUserClaims();

    /**
     * Returns a map of claim names to claim values that represent the authorized claims
     * when performing claim-based token revocation.
     *
     * @return
     */
    public abstract Map<String, String> allowedRevocationClaims();

    public boolean matchesRequiredUserClaims(AuthorizationRule rule) {
        return requiredUserClaims().entrySet().containsAll(rule.requiredUserClaims().entrySet());
    }

    public boolean matchesAllowedRevocationClaims(AuthorizationRule rule) {
        return allowedRevocationClaims().entrySet().containsAll(rule.allowedRevocationClaims().entrySet());
    }
}
