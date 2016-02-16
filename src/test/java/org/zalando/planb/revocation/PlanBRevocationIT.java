package org.zalando.planb.revocation;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.persistence.StoredRevocation;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("it")
public class PlanBRevocationIT extends AbstractSpringTest {

    private static final Logger log = LoggerFactory.getLogger(PlanBRevocationIT.class);

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
        log.info(response.getBody());
    }

    @Test
    public void jsonFieldsAreSnakeCase() {
        revocationStore.storeRevocation(StoredRevocation.builder().type(RevocationType.CLAIM).build());

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> response = rest.getForEntity(URI.create("http://localhost:" + port + "/revocations"),
                String.class);

        // TODO finish this test
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.info(response.getBody());
    }

}
