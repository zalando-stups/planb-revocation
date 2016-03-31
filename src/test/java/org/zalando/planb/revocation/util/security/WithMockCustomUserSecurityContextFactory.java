package org.zalando.planb.revocation.util.security;

import autovalue.shaded.com.google.common.common.collect.ImmutableMap;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * A {@link SecurityContext} factory for {@link WithMockCustomUser}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    /**
     * Creates a mock security context, which mocks an OAuth Request and an authentication token.
     * <p>
     * <p>The token details will be set with the provided {@link WithMockCustomUser}.</p>
     *
     * @param customUser a mock user which will be used to set the realm and uid in the authentication token.
     * @return a new {@link SecurityContext}.
     */
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {

        OAuth2Request request = new OAuth2Request(null, customUser.uid(), null, true, null, null,
                "http://localhost:8080/revocations", null, null);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(customUser.uid(), "N/A");
        token.setDetails(ImmutableMap.of("realm", customUser.realm(), "uid", customUser.uid()));

        OAuth2Authentication auth = new OAuth2Authentication(request, token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);

        return context;
    }
}
