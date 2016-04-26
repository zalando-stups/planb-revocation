package org.zalando.planb.revocation.api;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.zalando.planb.revocation.AbstractSpringIT;
import org.zalando.planb.revocation.Main;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.util.security.WithMockCustomUser;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;
import static org.zalando.planb.revocation.util.InstantTimestamp.FIVE_MINUTES_AGO;

/**
 * This tiny integration test demonstrates the usage of spring.jackson.property-naming-strategy property
 */
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true, value = "spring.jackson.property-naming-strategy=PASCAL_CASE_TO_CAMEL_CASE")
public class RevocationResourceObjectMapperIT extends AbstractSpringIT {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private RevocationStore revocationStore;

    private String basePath() {
        return "http://localhost:" + port;
    }

    /**
     * Please compare the response json body with the one in {@link RevocationResourceIT#testJsonFieldsInSnakeCase}:
     * "revoked_at" vs. "RevokedAt"
     */
    @Test
    @WithMockCustomUser
    public void testJsonFieldsInPascalCase() {
        revocationStore.storeRevocation(generateRevocation(RevocationType.TOKEN));

        final ResponseEntity<String> response = getRestTemplate()
                .exchange(get(URI.create(basePath() + "/revocations?from=" + FIVE_MINUTES_AGO.seconds())).build(),
                        String.class);
        final JSONObject jsonBody = new JSONObject(response.getBody());
        assertThat(jsonBody.getJSONArray("Revocations").getJSONObject(0).get("RevokedAt")).isNotNull();
    }
}
