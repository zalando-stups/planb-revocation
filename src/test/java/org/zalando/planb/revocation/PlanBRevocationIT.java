package org.zalando.planb.revocation;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.json.JSONObject;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.web.client.RestTemplate;

import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.persistence.StoredRevocation;

import lombok.extern.slf4j.Slf4j;

@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("it")
@Slf4j
public class PlanBRevocationIT extends AbstractSpringTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private RevocationStore revocationStore;

    @Test
    public void returnsHealth() {
        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> response = rest.getForEntity(URI.create("http://localhost:" + port + "/health"),
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void returnsSwaggerSpec() {
        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> response = rest.getForEntity(URI.create("http://localhost:" + port + "/swagger.json"),
                String.class);
        JSONObject jsonBody = new JSONObject(response.getBody());

        assertThat(jsonBody.get("swagger")).isNotNull();
    }

    @Test
    public void jsonFieldsAreSnakeCase() {

        // A Stored revocation always have a revokedAd field set to current time
        revocationStore.storeRevocation(new StoredRevocation(null, null, null));

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> response = rest.getForEntity(URI.create("http://localhost:" + port + "/revocations"),
                String.class);
        JSONObject jsonBody = new JSONObject(response.getBody());

        assertThat(jsonBody.getJSONArray("revocations").getJSONObject(0).get("revoked_at")).isNotNull();
    }
}
