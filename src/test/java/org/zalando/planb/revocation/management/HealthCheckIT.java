package org.zalando.planb.revocation.management;

import com.datastax.driver.core.Session;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.zalando.planb.revocation.Main;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.AFTER_METHOD;


@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true, value = "endpoints.health.time-to-live=0")
@ActiveProfiles("it")
public class HealthCheckIT {

    private static final ParameterizedTypeReference<Map<String, Object>> HEALTH_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule methodRule = new SpringMethodRule();

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Value("${local.management.port}")
    private int mgmtPort;

    @Autowired
    private Session session;

    // the test will break the cassandra session
    @DirtiesContext(methodMode = AFTER_METHOD)
    @Test
    public void testGetHealth() throws Exception {

        // first health check should succeed
        final Map<String, Object> health = pollHealth();
        assertThat(health).containsKey("cassandra");

        // destroy cassandra connection
        session.close();

        // second health check should fail
        try {
            pollHealth();
            failBecauseExceptionWasNotThrown(HttpServerErrorException.class);
        } catch (HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            assertThat(e.getStatusCode()).isEqualTo(SERVICE_UNAVAILABLE);
        }
    }

    private Map<String, Object> pollHealth() {
        return restTemplate.exchange(
                get(URI.create("http://localhost:" + mgmtPort + "/health"))
                        .accept(APPLICATION_JSON).build(),
                HEALTH_TYPE)
                .getBody();
    }

}
