package org.zalando.planb.revocation.api;


import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.zalando.planb.revocation.AbstractSpringTest;
import org.zalando.planb.revocation.Main;
import org.zalando.planb.revocation.domain.AuthorizationRule;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationRequest;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.persistence.AuthorizationRulesStore;

import java.net.URI;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.http.RequestEntity.post;

@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("it")
@Ignore
public class AuthorizationRulesIT extends AbstractSpringTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private AuthorizationRulesStore authorizationRulesStore;

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    private String basePath() {
        return "http://localhost:" + port;
    }


    @Test
    public void testRevocationByClaimIsUnauthorized() {
        addAuthorizationRule(of("uid", "test0"), of("realm", "/customers"));
        try {
            performRevocationWithToken(VALID_ACCESS_TOKEN);
            fail("The revocation should have been unauthorized");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(e.getResponseBodyAsString()).contains("{uid=rreis, sub=abcd}");
        }
    }

    @Test
    public void testRevocationBySingleClaimIsAuthorized() {
        addAuthorizationRule(of("sub", "test0"), of("realm", "/services"));
        ResponseEntity<HttpStatus> info = performRevocationWithClaims(VALID_ACCESS_TOKEN, of("realm", "/services"));
        assertThat(info.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testRevocationBySingleRuleMultipleClaimIsAuthorized() {
        addAuthorizationRule(of("sub", "test0"), of("realm", "/services"));
        ResponseEntity<HttpStatus> info = performRevocationWithClaims(VALID_ACCESS_TOKEN, of("realm", "/services", "sub", "test0"));
        assertThat(info.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testRevocationByMultipleClaimIsAuthorized() {
        addAuthorizationRule(of("sub", "test0"), of("realm", "/services", "sub", "test0"));
        try {
            performRevocationWithClaims(VALID_ACCESS_TOKEN, of("realm", "/services", "sub", "test0"));
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(e.getResponseBodyAsString()).contains("{uid=rreis, sub=abcd}");
        }
    }

    @Test
    public void testRevocationByMultipleClaimIsUnauthorized() {
        addAuthorizationRule(of("sub", "test0"), of("realm", "/services", "sub", "test0"));
        try {
            performRevocationWithClaims(VALID_ACCESS_TOKEN, of("realm", "/services"));
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @Test
    public void testRevocationWithNoRules() {
        try {
            performRevocationWithClaims(VALID_ACCESS_TOKEN, of("realm", "/services", "sub", "test0"));
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    private ResponseEntity<RevocationInfo> performRevocationWithToken(String token) {
        final RevocationRequest requestBody = generateRevocation(RevocationType.CLAIM);
        return restTemplate.exchange(post(URI.create(basePath() + "/revocations"))
                .header(HttpHeaders.AUTHORIZATION, token).body(requestBody), RevocationInfo.class);
    }

    private ResponseEntity<HttpStatus> performRevocationWithClaims(String token, Map<String, String> claims) {
        final RevocationRequest requestBody = generateClaimBasedRevocation(claims);
        return restTemplate.exchange(post(URI.create(basePath() + "/revocations"))
                .header(HttpHeaders.AUTHORIZATION, token).body(requestBody), HttpStatus.class);
    }

    private void addAuthorizationRule(Map<String, String> source, Map<String, String> target) {
        authorizationRulesStore.storeAccessRule(AuthorizationRule.builder()
                .sourceClaims(source)
                .targetClaims(target)
                .build());
    }
}
