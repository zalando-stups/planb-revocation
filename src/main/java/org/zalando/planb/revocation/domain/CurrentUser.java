package org.zalando.planb.revocation.domain;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Provides user information in the current Security Context.
 * <p>
 * <p>Whenever a revocation is posted, this object provides user information about the current security context for
 * auditing purposes.</p>
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class CurrentUser implements Supplier<String> {

    private static final String FORMAT = "%s/%s";

    private static final String REALM = "realm";

    private static final String UID = "uid";

    /**
     * Returns the user information in the current Spring security context.
     * <p>
     * <p>The information returned is comprised of the realm and the uid.</p>
     *
     * @return a String in the format {@code realm/uid}
     */
    @Override
    public String get() {

        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(auth -> (OAuth2Authentication) auth)
                .map(OAuth2Authentication::getUserAuthentication)
                .map(Authentication::getDetails)
                .map(details -> (Map<?, ?>) details)
                .map(details -> String.format(FORMAT, details.get(REALM), details.get(UID)))
                .orElseThrow(() -> new IllegalStateException("No authentication found in SecurityContext"));
    }
}
