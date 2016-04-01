package org.zalando.planb.revocation.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.post;

import java.net.URI;

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
import org.zalando.planb.revocation.AbstractSpringIT;
import org.zalando.planb.revocation.AbstractSpringTest;
import org.zalando.planb.revocation.Main;
import org.zalando.planb.revocation.domain.NotificationType;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.util.InstantTimestamp;

/**
 * Integration tests for the {@code /notifications} endpoint.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
public class NotificationResourceIT extends AbstractSpringIT {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private RevocationStore revocationStore;

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    private String basePath() {
        return "http://localhost:" + port;
    }

    /**
     * Tests {@code POST}ting a {@code REFRESH_FROM} as meta information.
     *
     * <p>Asserts that the request was successful, and that refresh information was stored with corresponding values.</p>
     */
    @Test
    public void testNotifyRefreshFrom() {

        ResponseEntity<String> response = restTemplate.exchange(post(URI.create(basePath() + "/notifications/" +
                NotificationType.REFRESH_FROM + "?value=" + InstantTimestamp.FIVE_MINUTES_AGO.seconds()))
                .header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).build(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(revocationStore.getRefresh().refreshFrom()).isEqualTo(InstantTimestamp.FIVE_MINUTES_AGO.seconds());
    }
}
