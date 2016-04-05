package org.zalando.planb.revocation.api;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.zalando.planb.revocation.AbstractSpringTest;
import org.zalando.planb.revocation.Main;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.persistence.AuthorizationRulesStore;
import org.zalando.planb.revocation.util.security.WithMockCustomUser;

import java.net.URI;

import static org.assertj.core.api.Assertions.fail;
import static org.springframework.http.RequestEntity.post;

@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("test")
public class AuthorizationRulesTest extends AbstractSpringTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private AuthorizationRulesStore authorizationRulesStore;

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    private String basePath() {
        return "http://localhost:" + port;
    }

    @Test
    @WithMockCustomUser
    public void testRevocationByClaimIsUnauthorized() {
        RevocationData requestBody = generateRevocation(RevocationType.CLAIM);

        try {
            restTemplate.exchange(post(URI.create(basePath() + "/revocations"))
                    .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).body(requestBody), RevocationInfo.class);
            fail("not implemented");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
