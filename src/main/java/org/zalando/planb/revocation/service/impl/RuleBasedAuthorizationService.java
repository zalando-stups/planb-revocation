package org.zalando.planb.revocation.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.zalando.planb.revocation.domain.RevocationRequest;
import org.zalando.planb.revocation.persistence.AuthorizationRulesStore;
import org.zalando.planb.revocation.service.RevocationAuthorizationService;

@Component
@Slf4j
public class RuleBasedAuthorizationService implements RevocationAuthorizationService{

    @Autowired
    private AuthorizationRulesStore authorizationRulesStore;

    @Override
    public void checkAuthorization(final RevocationRequest revocationRequest) {
        SecurityContext sc = SecurityContextHolder.getContext();
        log.info("checking auth");
    }
}
