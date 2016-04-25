package org.zalando.planb.revocation.util.security;

import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.config.properties.RevocationProperties;
import org.zalando.planb.revocation.domain.RevokedClaimsData;
import org.zalando.planb.revocation.service.impl.AbstractAuthorizationService;

public class ClaimRevocationAlwaysAuthorizedService extends AbstractAuthorizationService {

    public ClaimRevocationAlwaysAuthorizedService(
            final RevocationProperties revocationProperties,
            final CassandraProperties cassandraProperties) {
        super(revocationProperties, cassandraProperties);
    }

    @Override
    protected void checkClaimBasedRevocation(RevokedClaimsData claimsData) {
        // deliberately left empty
    }
}
