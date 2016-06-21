package org.zalando.planb.revocation.config;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.client.RestClientException;
import org.zalando.stups.oauth2.spring.server.DefaultTokenInfoRequestExecutor;
import org.zalando.stups.oauth2.spring.server.TokenInfoRequestExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Executor that allows several tokeninfo endpoints separated by "|", then requests
 * by order of definition in the string, and if an error happens, falls-back to the next one.
 *
 * @author vroldanbetan
 *
 */
public class FallbackTokenInfoRequestExecutor implements TokenInfoRequestExecutor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final static String URI_SEPARATOR = "\\,";

    private List<TokenInfoRequestExecutor> executors;

    public FallbackTokenInfoRequestExecutor(final String tokenInfoEndpointUrl) {
        executors = buildExecutorsFromMultipleURLs(tokenInfoEndpointUrl);
    }

    private List<TokenInfoRequestExecutor> buildExecutorsFromMultipleURLs(String tokenInfoEndpointUrl) {
        log.info("Token Info with fallback enabled: {}", tokenInfoEndpointUrl);
        final List<TokenInfoRequestExecutor> tokenInfoRequestExecutors = new ArrayList<>();
        for (String endpoint : tokenInfoEndpointUrl.split(URI_SEPARATOR)) {
            tokenInfoRequestExecutors.add(new DefaultTokenInfoRequestExecutor(endpoint.trim()));
        }
        return ImmutableList.copyOf(tokenInfoRequestExecutors);
    }

    @Override
    public Map<String, Object> getMap(String accessToken) {
        RuntimeException cachedException = new InvalidTokenException("Access Token not valid");
        Map<String, Object> result = Collections.emptyMap();
        for (TokenInfoRequestExecutor executor : executors) {
            try {
                result = executor.getMap(accessToken);
                if (result.containsKey("error")) {
                    log.warn("Token info responded {} for provided token: {}", result.get("error"), result.get("error_description"));
                } else {
                    return result;
                }
            } catch (RestClientException ex) {
                log.warn("Token info request failed: {}", ex.getMessage());
                cachedException = ex;
            }
        }
        if (!result.isEmpty()) {
            return result;
        }
        throw cachedException;
    }
}
