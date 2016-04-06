package org.zalando.planb.revocation.service;

import org.zalando.planb.revocation.domain.RevocationRequest;

public interface RevocationAuthorizationService {

    void checkAuthorization(RevocationRequest revocationRequest);
}
