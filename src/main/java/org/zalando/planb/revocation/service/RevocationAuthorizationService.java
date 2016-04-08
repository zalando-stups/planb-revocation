package org.zalando.planb.revocation.service;

import org.zalando.planb.revocation.domain.RevocationRequest;

/**
 * Determines whether a {@link RevocationRequest} is authorized or not
 * depending on a set of stored {@link org.zalando.planb.revocation.domain.AuthorizationRule rules}.
 */
public interface RevocationAuthorizationService {

    void checkAuthorization(RevocationRequest revocationRequest);
}
