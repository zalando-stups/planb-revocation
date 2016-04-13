package org.zalando.planb.revocation.util.security;

import org.zalando.planb.revocation.domain.RevokedClaimsData;
import org.zalando.planb.revocation.service.impl.AbstractAuthorizationService;

public class ClaimRevocationAlwaysAuthorizedService extends AbstractAuthorizationService{

    @Override
    protected void checkClaimBasedRevocation(RevokedClaimsData claimsData) {
        // deliberately left empty
    }
}
