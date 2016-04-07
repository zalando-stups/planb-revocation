package org.zalando.planb.revocation.domain;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class AuthorizationRule {

    private Map<String, String> sourceClaims;

    private Map<String, String> targetClaims;

    public boolean containsSourceClaims(AuthorizationRule rule) {
        return getSourceClaims().entrySet().containsAll(rule.getSourceClaims().entrySet());
    }

    public boolean containsTargetClaims(AuthorizationRule rule) {
        return getTargetClaims().entrySet().containsAll(rule.getTargetClaims().entrySet());
    }
}
