package org.zalando.planb.revocation.api;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.http.RequestEntity.get;
import static org.springframework.http.RequestEntity.post;

import java.net.URI;

import org.json.JSONObject;

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
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.domain.ClaimRevocationData;
import org.zalando.planb.revocation.domain.Problem;
import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.TokenRevocationData;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.persistence.StoredClaim;
import org.zalando.planb.revocation.persistence.StoredRevocation;
import org.zalando.planb.revocation.persistence.StoredToken;
import org.zalando.planb.revocation.util.MessageHasher;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("it")
public class RevocationResourceIT extends AbstractSpringTest {

    private static final long FIVE_MINUTES_AGO = System.currentTimeMillis() - 3000;

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private RevocationStore revocationStore;

    @Autowired
    private CassandraProperties cassandraProperties;

    @Autowired
    private MessageHasher messageHasher;

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    private String basePath() {
        return "http://localhost:" + port;
    }

    @Test
    public void testJsonFieldsInSnakeCase() {

        long currentTime = System.currentTimeMillis();

        // A Stored revocation always have a revokedAt field set to current time
        StoredRevocation revocation = new StoredRevocation(new StoredToken("abcdef"), RevocationType.TOKEN, "int-test");
        revocation.setRevokedAt(currentTime);
        revocationStore.storeRevocation(revocation);

        ResponseEntity<String> response = restTemplate.exchange(get(
                    URI.create(basePath() + "/revocations?from=" + FIVE_MINUTES_AGO)).build(), String.class);

        JSONObject jsonBody = new JSONObject(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jsonBody.getJSONArray("revocations").getJSONObject(0).get("revoked_at")).isNotNull();
    }

    @Test
    public void testGetEmptyRevocation() {
        ResponseEntity<RevocationInfo> response = restTemplate.exchange(get(
                    URI.create(basePath() + "/revocations?from=" + FIVE_MINUTES_AGO)).build(), RevocationInfo.class);
        RevocationInfo responseBody = response.getBody();

        assertThat(responseBody.getMeta()).isNull();
        assertThat(responseBody.getRevocations().isEmpty()).isTrue();
    }

    @Test
    public void testInsertRevocation() {
        Revocation requestBody = generateRevocation(RevocationType.GLOBAL);

        ResponseEntity<Revocation> responseEntity = restTemplate.exchange(post(URI.create(basePath() + "/revocations"))
                    .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).body(requestBody), Revocation.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Tests that when <code>GET</code>ing revocations, a <code>TOKEN</code> revocation is returned properly encoded and
     * encrypted. After inserting a known <code>TOKEN</code> revocation, performs a <code>GET</code> in the endpoint and
     * verifies that the response contains the same revocation information, containing the following:
     *
     * <ul>
     *   <li>A <code>token_hash</code> field, encoded and encrypted according to configuration;</li>
     *   <li>A <code>hash_algorithm</code> field, with the encryption algorithm used.</li>
     * </ul>
     *
     * Finally verifies that the result of encrypting and decoding the original value matches <code>token_hash</code>.
     */
    @Test
    public void testSHA256TokenHashing() {
        Revocation tokenRevocation = generateRevocation(RevocationType.TOKEN);
        TokenRevocationData revocationData = (TokenRevocationData) tokenRevocation.getData();
        String unhashedToken = revocationData.getTokenHash();

        // Store in backend
        StoredRevocation storedRevocation = new StoredRevocation(new StoredToken(revocationData.getTokenHash()),
                tokenRevocation.getType(), "int-test");
        storedRevocation.setRevokedAt(tokenRevocation.getRevokedAt());
        revocationStore.storeRevocation(storedRevocation);

        // Get revocations. We should get the one we stored
        ResponseEntity<RevocationInfo> response = restTemplate.exchange(get(
                    URI.create(basePath() + "/revocations?from=" + FIVE_MINUTES_AGO)).build(), RevocationInfo.class);

        TokenRevocationData fromService = (TokenRevocationData) response.getBody().getRevocations().get(0).getData();
        assertThat(fromService.getHashAlgorithm()).isEqualTo(messageHasher.getHashers().get(RevocationType.TOKEN)
                .getAlgorithm());

        String hashedToken = messageHasher.hashAndEncode(RevocationType.TOKEN, unhashedToken);
        assertThat(fromService.getTokenHash()).isEqualTo(hashedToken);
    }

    /**
     * Tests that when <code>GET</code>ing revocations, a <code>CLAIM</code> revocation is returned properly encoded and
     * encrypted. After inserting a known <code>CLAIM</code> revocation, performs a <code>GET</code> in the endpoint and
     * verifies that the response contains the same revocation information, containing the following:
     *
     * <ul>
     *   <li>A <code>value_hash</code> field, encoded and encrypted according to configuration;</li>
     *   <li>A <code>hash_algorithm</code> field, with the encryption algorithm used.</li>
     * </ul>
     *
     * Finally verifies that the result of encrypting and decoding the original value matches <code>value_hash</code>.
     */
    @Test
    public void testClaimHashing() {
        Revocation claimRevocation = generateRevocation(RevocationType.CLAIM);
        ClaimRevocationData revocationData = (ClaimRevocationData) claimRevocation.getData();
        String unhashedValue = revocationData.getValueHash();

        // Store in backend
        StoredRevocation storedRevocation = new StoredRevocation(new StoredClaim(revocationData.getName(),
                    revocationData.getValueHash(), revocationData.getIssuedBefore()), claimRevocation.getType(),
                "int-test");
        storedRevocation.setRevokedAt(claimRevocation.getRevokedAt());
        revocationStore.storeRevocation(storedRevocation);

        // Get revocations. We should get the one we stored
        ResponseEntity<RevocationInfo> response = restTemplate.exchange(get(
                    URI.create(basePath() + "/revocations?from=" + FIVE_MINUTES_AGO)).build(), RevocationInfo.class);

        // Assert that it contains revocation
        ClaimRevocationData fromService = (ClaimRevocationData) response.getBody().getRevocations().get(0).getData();
        assertThat(fromService.getHashAlgorithm()).isEqualTo(messageHasher.getHashers().get(RevocationType.CLAIM)
                .getAlgorithm());

        String hashedValue = messageHasher.hashAndEncode(RevocationType.CLAIM, unhashedValue);
        assertThat(fromService.getValueHash()).isEqualTo(hashedValue);
    }

    /**
     * Tests that when {@code GET}ing revocations with a timestamp too old - according to a Cassandra Property, returns
     * an error.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testTimestampTooOldOnGet() throws Exception {

        long tooOldTimeStamp = System.currentTimeMillis() - cassandraProperties.getMaxTimeDelta() - 1000;

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(get(
                    URI.create(basePath() + "/revocations?from=" + tooOldTimeStamp)).build(), String.class);
        } catch(HttpClientErrorException e) {
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

    }

}
