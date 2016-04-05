package org.zalando.planb.revocation.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthorizationRule {

    private String sourceClaimName;

    private String sourceClaimValue;

    private String targetClaimName;

    private String targetClaimValue;
}
