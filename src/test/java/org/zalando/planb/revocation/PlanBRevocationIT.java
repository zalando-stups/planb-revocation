package org.zalando.planb.revocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;

import java.io.IOException;
import java.net.URI;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.persistence.StoredRevocation;
import org.zalando.planb.revocation.persistence.StoredToken;

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

        long currentTime = System.currentTimeMillis();

        // A Stored revocation always have a revokedAd field set to current time
        StoredRevocation revocation = new StoredRevocation(new StoredToken("abcdef"), RevocationType.TOKEN, "int-test");
        revocation.setRevokedAt(currentTime);

        revocationStore.storeRevocation(revocation);

        revocationStore.getRevocations(currentTime - 100000);

        RestTemplate rest = new RestTemplate();
        rest.getInterceptors().add(new ClientHttpRequestInterceptor() {
            
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                    throws IOException {
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer 987654321");
                return execution.execute(request, body);
            }
        });
        ResponseEntity<String> response = rest.getForEntity(URI.create(
                    "http://localhost:" + port + "/revocations?from=" + (currentTime - 1000)), String.class);

        JSONObject jsonBody = new JSONObject(response.getBody());

        assertThat(jsonBody.getJSONArray("revocations").getJSONObject(0).get("revoked_at")).isNotNull();
    }
}
