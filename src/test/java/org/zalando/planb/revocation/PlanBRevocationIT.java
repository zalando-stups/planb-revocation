package org.zalando.planb.revocation;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("it")
@Slf4j
public class PlanBRevocationIT extends AbstractSpringTest {

    @Value("${local.server.port}")
    private int port;

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
}
