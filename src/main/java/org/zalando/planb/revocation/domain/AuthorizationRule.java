package org.zalando.planb.revocation.domain;


import org.immutables.value.Value;

import java.util.Collections;
import java.util.Map;

@Value.Immutable
public abstract class AuthorizationRule {

    @Value.Default
    public Map<String, String> requiredUserClaims() {
        return Collections.EMPTY_MAP;
    }

    @Value.Default
    public Map<String, String> allowedRevocationClaims() {
        return Collections.EMPTY_MAP;
    }

    public boolean containsSourceClaims(AuthorizationRule rule) {
        return requiredUserClaims().entrySet().containsAll(rule.requiredUserClaims().entrySet());
    }

    public boolean containsTargetClaims(AuthorizationRule rule) {
        return allowedRevocationClaims().entrySet().containsAll(rule.allowedRevocationClaims().entrySet());
    }
}
