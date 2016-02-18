package org.zalando.planb.revocation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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
import org.springframework.web.client.RestTemplate;
import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.TokenRevocation;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.persistence.StoredRevocation;
import org.zalando.planb.revocation.persistence.StoredToken;

import java.net.URI;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.http.RequestEntity.get;

/**
 * Created by rreis on 17/02/16.
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

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    private String basePath() {
        return "http://localhost:" + port;
    }

    @Test
    public void testJsonFieldsInSnakeCase() {

        long currentTime = System.currentTimeMillis();

        // A Stored revocation always have a revokedAd field set to current time
        StoredRevocation revocation = new StoredRevocation(new StoredToken("abcdef"), RevocationType.TOKEN, "int-test");
        revocation.setRevokedAt(currentTime);
        revocationStore.storeRevocation(revocation);

        ResponseEntity<String> response = restTemplate.exchange(get(URI.create(basePath() +
                "/revocations?from=" + FIVE_MINUTES_AGO))
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).build(), String.class);

        JSONObject jsonBody = new JSONObject(response.getBody());


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jsonBody.getJSONArray("revocations").getJSONObject(0).get("revoked_at")).isNotNull();
    }

    @Test
    public void testGetEmptyRevocation() {
        ResponseEntity<RevocationInfo> response = restTemplate.exchange(get(URI.create(basePath() +
                "/revocations?from=" + FIVE_MINUTES_AGO))
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).build(), RevocationInfo.class);
        RevocationInfo responseBody = response.getBody();

        assertThat(responseBody.getMeta()).isNull();
        assertThat(responseBody.getRevocations().isEmpty()).isTrue();
    }

    @Test
    public void testInsertRevocation() {
        Revocation requestBody = new Revocation();
        requestBody.setType(RevocationType.TOKEN);
        requestBody.setRevokedAt(System.currentTimeMillis());

        TokenRevocation revocation = new TokenRevocation();
        revocation.setTokenHash("aslkjdlaksdj");
        requestBody.setData(revocation);

        assertThat(restTemplate.exchange(post(URI.create(basePath() + "/revocations"))
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).body(requestBody), Revocation.class)
                .getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
