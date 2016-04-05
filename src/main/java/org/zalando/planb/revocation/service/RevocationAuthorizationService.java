package org.zalando.planb.revocation.service;

import org.zalando.planb.revocation.domain.RevocationData;

public interface RevocationAuthorizationService {

    void checkAuthorization(RevocationData revocationData);
}
