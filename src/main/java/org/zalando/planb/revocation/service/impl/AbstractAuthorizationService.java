package org.zalando.planb.revocation.service.impl;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestBody;
import org.zalando.planb.revocation.api.exception.AncientRevocationException;
import org.zalando.planb.revocation.api.exception.FutureRevocationException;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.config.properties.RevocationProperties;
import org.zalando.planb.revocation.domain.RevocationRequest;
import org.zalando.planb.revocation.domain.RevokedClaimsData;
import org.zalando.planb.revocation.domain.RevokedData;
import org.zalando.planb.revocation.domain.RevokedGlobal;
import org.zalando.planb.revocation.domain.RevokedTokenData;
import org.zalando.planb.revocation.service.RevocationAuthorizationService;
import org.zalando.planb.revocation.util.UnixTimestamp;

public abstract class AbstractAuthorizationService implements RevocationAuthorizationService {

    private final RevocationProperties revocationProperties;

    private final CassandraProperties cassandraProperties;

    protected AbstractAuthorizationService(RevocationProperties revocationProperties, CassandraProperties cassandraProperties) {
        this.revocationProperties = revocationProperties;
        this.cassandraProperties = cassandraProperties;
    }

    @Override
    public void checkAuthorization(final RevocationRequest revocationRequest) {
        checkFutureRevocations(revocationRequest);
        checkAncientRevocation(revocationRequest);
        doCheckAuthorization(revocationRequest);
    }

    private void doCheckAuthorization(RevocationRequest revocationRequest) {
        final RevokedData data = revocationRequest.data();
        switch (revocationRequest.type()) {
            case TOKEN:
                // Token revocations are generally allowed, as it implies a scope at least
                break;
            case CLAIM:
                checkClaimBasedRevocation((RevokedClaimsData)data);
                break;
            case GLOBAL:
                // We don't allow GLOBAL revocations
                throw new AccessDeniedException("Permission denied to create global revocations.");
        }
    }

    private void checkFutureRevocations(@RequestBody RevocationRequest revocation) {
        if (getIssuedBeforeFromData(revocation) > UnixTimestamp.now() + revocationProperties.getTimestampThreshold()) {
            throw new FutureRevocationException();
        }
    }

    private void checkAncientRevocation(@RequestBody RevocationRequest revocation) {
        if (getIssuedBeforeFromData(revocation) < UnixTimestamp.now() - cassandraProperties.getMaxTimeDelta()) {
            throw new AncientRevocationException();
        }
    }

    private Integer getIssuedBeforeFromData(@RequestBody RevocationRequest revocation) {
        Integer timestamp = null;
        switch (revocation.type()) {
            case TOKEN:
                timestamp = ((RevokedTokenData) revocation.data()).issuedBefore();
                break;
            case CLAIM:
                timestamp = ((RevokedClaimsData) revocation.data()).issuedBefore();
                break;
            case GLOBAL:
                timestamp = ((RevokedGlobal) revocation.data()).issuedBefore();
                break;
        }
        return timestamp;
    }

    protected abstract void checkClaimBasedRevocation(final RevokedClaimsData claimsData);

}
