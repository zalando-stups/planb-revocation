package org.zalando.planb.revocation.api;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.zalando.planb.revocation.AbstractSpringIT;
import org.zalando.planb.revocation.Main;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.domain.ImmutableAuthorizationRule;
import org.zalando.planb.revocation.domain.NotificationType;
import org.zalando.planb.revocation.domain.Problem;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationList;
import org.zalando.planb.revocation.domain.RevocationRequest;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.RevokedClaimsData;
import org.zalando.planb.revocation.domain.RevokedClaimsInfo;
import org.zalando.planb.revocation.domain.RevokedTokenData;
import org.zalando.planb.revocation.domain.RevokedTokenInfo;
import org.zalando.planb.revocation.persistence.AuthorizationRulesStore;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.util.ApiGuildCompliance;
import org.zalando.planb.revocation.util.InstantTimestamp;
import org.zalando.planb.revocation.util.MessageHasher;
import org.zalando.planb.revocation.util.security.WithMockCustomUser;

import java.net.URI;
import java.util.Collection;

import static com.google.common.collect.ImmutableMap.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.http.RequestEntity.post;

/**
 * Integration tests for the {@code /revocations} endpoint.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
public class RevocationResourceIT extends AbstractSpringIT {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private RevocationStore revocationStore;

    @Autowired
    private AuthorizationRulesStore authorizationRulesStore;

    @Autowired
    private CassandraProperties cassandraProperties;

    @Autowired
    private MessageHasher messageHasher;

    private String basePath() {
        return "http://localhost:" + port;
    }

    @Test
    @WithMockCustomUser
    public void testJsonFieldsInSnakeCase() {

        RevocationRequest revocation = generateRevocation(RevocationType.TOKEN);
        revocationStore.storeRevocation(revocation);

        ResponseEntity<String> response = getRestTemplate().exchange(get(
                URI.create(basePath() + "/revocations?from=" + InstantTimestamp.FIVE_MINUTES_AGO.seconds()))
                .header("X-Forwarded-For", "0.0.8.15") // to test the request ip logging when forwarded from a load balancer
                .build(), String.class);

        JSONObject jsonBody = new JSONObject(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jsonBody.getJSONArray("revocations").getJSONObject(0).get("revoked_at")).isNotNull();
    }

    /**
     * Tests {@code GET}ting revocations with an empty storage. Asserts that no revocations are returned, and that the
     * meta section contains information about the maximum time delta allowed.
     */
    @Test
    public void testGetEmptyRevocations() {
        ResponseEntity<RevocationList> response = getRestTemplate().exchange(get(
                URI.create(basePath() + "/revocations?from=" + InstantTimestamp.FIVE_MINUTES_AGO.seconds()))
                .build(), RevocationList.class);
        RevocationList responseBody = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        /*
         * Jackson probably unmarshalls this value to the Number with the lowest resolution possible. That's why I'm
         * using a toString here.
         */
        int maxTimeDelta = Integer.valueOf(responseBody.meta().get(NotificationType.MAX_TIME_DELTA).toString());
        assertThat(maxTimeDelta).isEqualTo(cassandraProperties.getMaxTimeDelta());
        assertThat(responseBody.revocations().isEmpty()).isTrue();
    }

    /**
     * Tests {@code GET}ting revocations with {@code REFRESH_FROM} as meta information.
     * <p>
     * <p>
     * <p>Asserts that refresh information is included, and that it matches the value persisted.</p>
     */
    @Test
    @WithMockCustomUser
    public void testGetRefreshFromInMeta() {
        revocationStore.storeRefresh(InstantTimestamp.FIVE_MINUTES_AGO.seconds());

        ResponseEntity<RevocationList> response = getRestTemplate().exchange(get(
                URI.create(basePath() + "/revocations?from=" + (InstantTimestamp.FIVE_MINUTES_AGO.seconds())))
                .build(), RevocationList.class);
        RevocationList responseBody = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        /*
         * Jackson probably unmarshalls this value to the a Number with the lowest resolution possible. That's why I'm
         * using a toString here.
         */
        int refreshTimestampRetrieved = Integer.valueOf(responseBody.meta().get(NotificationType.REFRESH_TIMESTAMP)
                .toString());
        int refreshFromRetrieved = Integer.valueOf(responseBody.meta().get(NotificationType.REFRESH_FROM).toString());

        assertThat(refreshTimestampRetrieved).isNotNull();
        assertThat(refreshFromRetrieved).isEqualTo(InstantTimestamp.FIVE_MINUTES_AGO.seconds());
    }

    /**
     * Verifies that a revocation is properly inserted.
     */
    @Test
    public void testPostRevocation() {
        RevocationRequest requestBody = generateRevocation(RevocationType.TOKEN);

        ResponseEntity<RevocationInfo> responseEntity = getRestTemplate()
                .exchange(post(URI.create(basePath() + "/revocations"))
                        .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                        .body(requestBody), RevocationInfo.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Collection<RevocationData> storedRevocations = revocationStore.getRevocations(
                InstantTimestamp.FIVE_MINUTES_AGO.seconds());

        assertThat(storedRevocations.size()).isEqualTo(1);
        assertThat(storedRevocations.iterator().next().revocationRequest().type()).isEqualTo(RevocationType.TOKEN);
    }

    /**
     * Tests that when POSTing a {@code GLOBAL} revocation, returns {@code HTTP FORBIDDEN} and a problem description.
     */
    @Test
    public void testUnauthorizedWhenPostingGlobal() {
        RevocationRequest requestBody = generateRevocation(RevocationType.GLOBAL);

        try {
            getRestTemplate().exchange(post(URI.create(basePath() + "/revocations"))
                    .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN)
                    .body(requestBody), RevocationInfo.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(ApiGuildCompliance.isStandardProblem(e.getResponseBodyAsString())).isTrue();
        }

        assertThat(revocationStore.getRevocations(InstantTimestamp.FIVE_MINUTES_AFTER.seconds())).isEmpty();
    }

    @Test
    public void testInsertClaimRevocation() {
        authorizationRulesStore.store(ImmutableAuthorizationRule.builder()
                .requiredUserClaims(of("sub", "test0"))
                .allowedRevocationClaims(of("realm", "/services"))
                .build());

        RevocationRequest requestBody = generateClaimBasedRevocation(of("realm", "/services"));

        ResponseEntity<RevocationInfo> responseEntity = getRestTemplate().exchange(post(URI.create(basePath() + "/revocations"))
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).body(requestBody), RevocationInfo.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Collection<RevocationData> storedRevocations = revocationStore.getRevocations(
                InstantTimestamp.FIVE_MINUTES_AGO.seconds());

        assertThat(storedRevocations).isNotEmpty();
        assertThat(storedRevocations.stream().filter(r -> r.revocationRequest().type() == RevocationType.CLAIM)
                .count()).isGreaterThan
                (0);
    }

    /**
     * Tests that when inserting revocations with no access token, a HTTP {@code UNAUTHORIZED} is returned.
     */
    @Test
    public void testUnauthorizedWhenNoTokenInInsert() {

        RevocationRequest requestBody = generateRevocation(RevocationType.GLOBAL);

        try {
            ResponseEntity<RevocationInfo> responseEntity = getRestTemplate().exchange(post(
                    URI.create(basePath() + "/revocations")).body(requestBody), RevocationInfo.class);
            failBecauseExceptionWasNotThrown(HttpClientErrorException.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            /*
             * Still need to return a Problem, but the ControllerAdvice isn't being called. Maybe this can help:
             * https://stackoverflow.com/questions/30335157/make-simple-servlet-filter-work-with-controlleradvice
             */
        }
    }

    /**
     * Tests that when inserting revocations with a wrong access token, a HTTP {@code UNAUTHORIZED} is returned.
     * <p>
     * <p>
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testUnauthorizedWhenInvalidTokenInInsert() {

        RevocationRequest requestBody = generateRevocation(RevocationType.GLOBAL);

        try {
            getRestTemplate().exchange(post(URI.create(basePath() + "/revocations")).header(HttpHeaders.AUTHORIZATION,
                    INVALID_ACCESS_TOKEN).body(requestBody), RevocationInfo.class);
            failBecauseExceptionWasNotThrown(HttpClientErrorException.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            /*
             * Still need to return a Problem, but the ControllerAdvice isn't being called. Maybe this can help:
             * https://stackoverflow.com/questions/30335157/make-simple-servlet-filter-work-with-controlleradvice
             */
        }
    }

    /**
     * Tests that when there's a Server Error returned from the Token Info Endpoint, a HTTP
     * {@code INTERNAL SERVER ERROR} is returned.
     * <p>
     * <p>
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testServerErrorOnTokenInfo() {
        RevocationRequest requestBody = generateRevocation(RevocationType.GLOBAL);

        try {
            getRestTemplate().exchange(post(URI.create(basePath() + "/revocations")).header(HttpHeaders.AUTHORIZATION,
                    SERVER_ERROR_ACCESS_TOKEN).body(requestBody), RevocationInfo.class);
            failBecauseExceptionWasNotThrown(HttpServerErrorException.class);
        } catch (HttpServerErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            /*
             * Still need to return a Problem, but the ControllerAdvice isn't being called. Maybe this can help:
             * https://stackoverflow.com/questions/30335157/make-simple-servlet-filter-work-with-controlleradvice
             */
        }
    }

    /**
     * Tests that when <code>GET</code>ing revocations, a <code>TOKEN</code> revocation is returned properly encoded and
     * encrypted. After inserting a known <code>TOKEN</code> revocation, performs a <code>GET</code> in the endpoint and
     * verifies that the response contains the same revocation information, containing the following:
     * <p>
     * <p>
     * <ul>
     * <li>A <code>token_hash</code> field, encoded and encrypted according to configuration;</li>
     * <li>A <code>hash_algorithm</code> field, with the encryption algorithm used.</li>
     * </ul>
     * <p>
     * <p>Finally verifies that the result of encrypting and decoding the original value matches <code>
     * token_hash</code>.
     */
    @Test
    @WithMockCustomUser
    public void testSHA256TokenHashing() {
        RevocationRequest tokenRevocation = generateRevocation(RevocationType.TOKEN);
        RevokedTokenData revocationData = (RevokedTokenData) tokenRevocation.data();
        String unhashedToken = revocationData.token();

        // Store in backend
        revocationStore.storeRevocation(tokenRevocation);

        // Get revocations. We should get the one we stored
        ResponseEntity<RevocationList> response = getRestTemplate().exchange(get(
                URI.create(basePath() + "/revocations?from=" + InstantTimestamp.FIVE_MINUTES_AGO.seconds()))
                .build(), RevocationList.class);

        RevokedTokenInfo fromService = (RevokedTokenInfo) response.getBody().revocations().get(0).data();
        assertThat(fromService.hashAlgorithm())
                .isEqualTo(messageHasher.hashingAlgorithms().get(RevocationType.TOKEN).getAlgorithm());

        String hashedToken = messageHasher.hashAndEncode(RevocationType.TOKEN, unhashedToken);
        assertThat(fromService.tokenHash()).isEqualTo(hashedToken);
    }

    /**
     * Tests that when <code>GET</code>ing revocations, a <code>CLAIM</code> revocation is returned properly encoded and
     * encrypted. After inserting a known <code>CLAIM</code> revocation, performs a <code>GET</code> in the endpoint and
     * verifies that the response contains the same revocation information, containing the following:
     * <p>
     * <p>
     * <ul>
     * <li>A <code>value_hash</code> field, encoded and encrypted according to configuration;</li>
     * <li>A <code>hash_algorithm</code> field, with the encryption algorithm used.</li>
     * </ul>
     * <p>
     * <p>Finally verifies that the result of encrypting and decoding the original value matches <code>
     * value_hash</code>.
     */
    @Test
    @WithMockCustomUser
    public void testClaimHashing() {
        RevocationRequest claimRevocation = generateRevocation(RevocationType.CLAIM);
        revocationStore.storeRevocation(claimRevocation);

        // Get revocations. We should get the one we stored
        ResponseEntity<RevocationList> response = getRestTemplate().exchange(get(
                URI.create(basePath() + "/revocations?from=" + InstantTimestamp.FIVE_MINUTES_AGO.seconds()))
                .build(), RevocationList.class);

        // Assert that it contains revocation
        RevokedClaimsInfo fromService = (RevokedClaimsInfo) response.getBody().revocations().get(0).data();
        assertThat(fromService.hashAlgorithm())
                .isEqualTo(messageHasher.hashingAlgorithms().get(RevocationType.CLAIM).getAlgorithm());

        String hashedValue = messageHasher.hashAndEncode(RevocationType.CLAIM, ((RevokedClaimsData) claimRevocation
                .data()).claims().values());
        assertThat(fromService.valueHash()).isEqualTo(hashedValue);
    }

    /**
     * Tests that when {@code GET}ing revocations with a timestamp too old - according to {@link CassandraProperties} -,
     * returns an error.
     * <p>
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testTimestampTooOldOnGet() {

        int tooOldTimeStamp = InstantTimestamp.FIVE_MINUTES_AGO.seconds() - cassandraProperties.getMaxTimeDelta();

        try {
            getRestTemplate().exchange(get(URI.create(basePath() + "/revocations?from=" + tooOldTimeStamp)).build(),
                    String.class);
            failBecauseExceptionWasNotThrown(HttpClientErrorException.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(ApiGuildCompliance.isStandardProblem(e.getResponseBodyAsString())).isTrue();
        }
    }

    /**
     * Tests that when {@code GET}ing revocations with a timestamp almost close to the maximum Time Delta - according to
     * {@link CassandraProperties}, returns an valid response.
     */
    @Test
    public void testTimestampNotTooOldOnGet() {

        int notTooOldTimeStamp = InstantTimestamp.NOW.seconds() - cassandraProperties.getMaxTimeDelta() + 60;

        ResponseEntity<String> response = getRestTemplate().exchange(get(
                URI.create(basePath() + "/revocations?from=" + notTooOldTimeStamp)).build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
