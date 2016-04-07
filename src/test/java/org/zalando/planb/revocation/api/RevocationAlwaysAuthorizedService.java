package org.zalando.planb.revocation.api;

import lombok.extern.slf4j.Slf4j;
import org.zalando.planb.revocation.domain.RevocationRequest;
import org.zalando.planb.revocation.service.RevocationAuthorizationService;

@Slf4j
public class RevocationAlwaysAuthorizedService implements RevocationAuthorizationService {

    @Override
    public void checkAuthorization(final RevocationRequest revocationRequest) {
        // left empty intentionally. Should authorize all revocations
        log.info("Request {} was authorized by {}", revocationRequest, RevocationAlwaysAuthorizedService.class.getName());
    }
}
