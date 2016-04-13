package org.zalando.planb.revocation.service.impl;

import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.zalando.planb.revocation.api.exception.RevocationUnauthorizedException;
import org.zalando.planb.revocation.domain.AuthorizationRule;
import org.zalando.planb.revocation.domain.ImmutableAuthorizationRule;
import org.zalando.planb.revocation.domain.RevokedClaimsData;
import org.zalando.planb.revocation.persistence.AuthorizationRulesStore;

import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RuleBasedClaimRevocationAuthorizationService extends AbstractAuthorizationService {

    @Autowired
    private AuthorizationRulesStore authorizationRulesStore;

    protected void checkClaimBasedRevocation(final RevokedClaimsData claimsData) {
        final AuthorizationRule sourceRule = ImmutableAuthorizationRule
                .builder()
                .requiredUserClaims(getSourceClaims()).build();
        final AuthorizationRule targetRule = ImmutableAuthorizationRule
                .builder()
                .allowedRevocationClaims(claimsData.claims()).build();
        final Collection<AuthorizationRule> sourceRules = authorizationRulesStore.retrieveByMatchingAllowedClaims(targetRule);
        sourceRules.stream()
                .filter(sourceRule::matchesRequiredUserClaims)
                .findAny()
                .orElseThrow(() -> new RevocationUnauthorizedException(targetRule));
    }

    private Map<String, String> getSourceClaims() {
        String accessToken = Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(auth -> (OAuth2Authentication) auth)
                .map(OAuth2Authentication::getUserAuthentication)
                .map(Authentication::getDetails)
                .map(theDetails -> (Map<?, ?>) theDetails)
                .map(m -> (String) m.get("access_token"))
                .orElseThrow(() -> new IllegalStateException("Could not find access_token in SecurityContext"));
        try {
            Map<String, Object> sourceClaims = JWTParser.parse(accessToken).getJWTClaimsSet().getClaims();
            return sourceClaims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
        } catch (ParseException e) {
            throw new IllegalStateException("Could not parse source claims in service token", e);
        }
    }
}
