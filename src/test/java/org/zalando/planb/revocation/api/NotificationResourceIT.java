package org.zalando.planb.revocation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.failBecauseExceptionWasNotThrown;

import static org.springframework.http.RequestEntity.get;
import static org.springframework.http.RequestEntity.post;

import java.net.URI;
import java.util.EnumMap;

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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import org.zalando.planb.revocation.AbstractSpringTest;
import org.zalando.planb.revocation.Main;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.domain.Problem;
import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationFlag;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.persistence.StoredRevocation;
import org.zalando.planb.revocation.persistence.StoredToken;
import org.zalando.planb.revocation.util.ApiGuildCompliance;

/**
 * Integration tests for the {@code /notifications} endpoint.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("it")
public class NotificationResourceIT extends AbstractSpringTest {

    private static final long FIVE_MINUTES_AGO = System.currentTimeMillis() - 3000;

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private RevocationStore revocationStore;

    @Autowired
    private CassandraProperties cassandraProperties;

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    private String basePath() {
        return "http://localhost:" + port;
    }

    @Test
    public void testInsertRefreshFrom() {

        EnumMap<RevocationFlag, Object> metaInfo = new EnumMap<>(RevocationFlag.class);
        metaInfo.put(RevocationFlag.REFRESH_FROM, FIVE_MINUTES_AGO);


        // TODO implement
//        // A Stored revocation always have a revokedAt field set to current time
//        StoredRevocation revocation = new StoredRevocation(new StoredToken("abcdef"), RevocationType.TOKEN, "int-test");
//        revocation.setRevokedAt(currentTime);
//        revocationStore.storeRevocation(revocation);
//
//        ResponseEntity<String> response = restTemplate.exchange(post(URI.create(basePath() + "/notifications")).body()
//                    .build(), String.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(jsonBody.getJSONArray("revocations").getJSONObject(0).get("revoked_at")).isNotNull();
    }

    /**
     * Tests {@code GET}ting revocations with an empty storage. Asserts that no revocations are returned, and that the
     * meta section contains information about the maximum time delta allowed.
     */
    @Test
    public void testGetEmptyRevocation() {
        ResponseEntity<RevocationInfo> response = restTemplate.exchange(get(
                    URI.create(basePath() + "/revocations?from=" + FIVE_MINUTES_AGO)).build(), RevocationInfo.class);
        RevocationInfo responseBody = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        /*
         * Jackson probably unmarshalls this value to the a Number with the lowest resolution possible. That's why I'm
         * using a toString here.
         */
        Long maxTimeDelta = Long.valueOf(responseBody.getMeta().get(RevocationFlag.MAX_TIME_DELTA).toString());
        assertThat(maxTimeDelta).isEqualTo(cassandraProperties.getMaxTimeDelta());

        assertThat(responseBody.getRevocations().isEmpty()).isTrue();
    }

    @Test
    public void testInsertRevocation() {
        Revocation requestBody = generateRevocation(RevocationType.GLOBAL);

        ResponseEntity<Revocation> responseEntity = restTemplate.exchange(post(URI.create(basePath() + "/revocations"))
                    .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).body(requestBody), Revocation.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    /**
     * Tests that when inserting revocations with no access token, a HTTP {@code UNAUTHORIZED} is returned.
     */
    @Test
    public void testUnauthorizedWhenNoTokenInInsert() {

        // TODO finish, need to catch Exception
        Revocation requestBody = generateRevocation(RevocationType.GLOBAL);

        try {
            ResponseEntity<Revocation> responseEntity = restTemplate.exchange(post(
                        URI.create(basePath() + "/revocations")).body(requestBody), Revocation.class);
            failBecauseExceptionWasNotThrown(HttpClientErrorException.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Tests that when inserting revocations with a wrong access token, a HTTP {@code UNAUTHORIZED} is returned.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testUnauthorizedWhenInvalidTokenInInsert() {

        // TODO finish, need to catch Exception
        Revocation requestBody = generateRevocation(RevocationType.GLOBAL);

        try {
            restTemplate.exchange(post(URI.create(basePath() + "/revocations")).header(HttpHeaders.AUTHORIZATION,
                    INVALID_ACCESS_TOKEN).body(requestBody), Revocation.class);
            failBecauseExceptionWasNotThrown(HttpClientErrorException.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Tests that when there's a Server Error returned from the Token Info Endpoint, a HTTP
     * {@code INTERNAL SERVER ERROR} is returned.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testServerErrorOnTokenInfo() {
        Revocation requestBody = generateRevocation(RevocationType.GLOBAL);

        try {
            restTemplate.exchange(post(URI.create(basePath() + "/revocations")).header(HttpHeaders.AUTHORIZATION,
                    SERVER_ERROR_ACCESS_TOKEN).body(requestBody), Revocation.class);
            failBecauseExceptionWasNotThrown(HttpServerErrorException.class);
        } catch (HttpServerErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Tests that when {@code GET}ing revocations with a timestamp too old - according to {@link CassandraProperties} -,
     * returns an error.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testTimestampTooOldOnGet() {

        long tooOldTimeStamp = System.currentTimeMillis() - cassandraProperties.getMaxTimeDelta() - 1000;

        try {
            restTemplate.exchange(get(URI.create(basePath() + "/revocations?from=" + tooOldTimeStamp)).build(),
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

        long notTooOldTimeStamp = System.currentTimeMillis() - cassandraProperties.getMaxTimeDelta() + 1000;

        ResponseEntity<String> response = restTemplate.exchange(get(
                    URI.create(basePath() + "/revocations?from=" + notTooOldTimeStamp)).build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
