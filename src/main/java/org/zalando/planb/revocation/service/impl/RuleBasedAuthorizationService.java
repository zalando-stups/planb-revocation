package org.zalando.planb.revocation.service.impl;

import com.nimbusds.jwt.JWTParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.zalando.planb.revocation.api.exception.RevocationUnauthorizedException;
import org.zalando.planb.revocation.domain.*;
import org.zalando.planb.revocation.persistence.AuthorizationRulesStore;
import org.zalando.planb.revocation.service.RevocationAuthorizationService;

import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RuleBasedAuthorizationService implements RevocationAuthorizationService {

    @Autowired
    private AuthorizationRulesStore authorizationRulesStore;

    @Override
    public void checkAuthorization(final RevocationRequest revocationRequest) {
        final RevokedData data = revocationRequest.getData();
        if (data instanceof RevokedClaimsData) {

            RevokedClaimsData claimsData = (RevokedClaimsData)data;
            AuthorizationRule sourceRule = ImmutableAuthorizationRule.builder().requiredUserClaims(getSourceClaims()).build();
            AuthorizationRule targetRule = ImmutableAuthorizationRule.builder().allowedRevocationClaims(claimsData.getClaims()).build();

            Collection<AuthorizationRule> sourceRules = authorizationRulesStore.withTargetClaims(targetRule);
            log.info("checking if contained...");
            sourceRules.stream().filter(sourceRule::containsSourceClaims).findAny().orElseThrow(() -> new RevocationUnauthorizedException(targetRule));
        }

    }

    private Map<String, String> getSourceClaims() {
        String accessToken = Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(auth -> (OAuth2Authentication) auth)
                .map(OAuth2Authentication::getUserAuthentication)
                .map(Authentication::getDetails)
                .map(theDetails -> (Map<?, ?>) theDetails)
                .map(m -> (String)m.get("access_token"))
                .orElseThrow(() -> new IllegalStateException("Could not find access_token in SecurityContext"));
        try {
            Map<String, Object> sourceClaims = JWTParser.parse(accessToken).getJWTClaimsSet().getClaims();
            return sourceClaims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
        } catch (ParseException e) {
            throw new IllegalStateException("Could not parse source claims in service token", e);
        }
    }
}
