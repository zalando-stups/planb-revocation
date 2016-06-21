package org.zalando.planb.revocation.config;

import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.zalando.planb.revocation.AbstractOAuthTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FallbackTokenInfoRequestExecutorTest extends AbstractOAuthTest {

    private Integer wireMockPort = Integer.valueOf(System.getProperty("wiremock.port", "10080"));

    @Test
    public void testSingleEndpointValidToken() {
        FallbackTokenInfoRequestExecutor executor =
                new FallbackTokenInfoRequestExecutor("http://localhost:" + wireMockPort + "/tokeninfo");
        Map<String, Object> result = executor.getMap(SAMPLE_TOKEN_2);
        assertThat(result).hasSize(7);
    }

    @Test(expected = HttpClientErrorException.class)
    public void testSingleEndpointInvalidToken() {
        FallbackTokenInfoRequestExecutor executor =
                new FallbackTokenInfoRequestExecutor("http://localhost:" + wireMockPort + "/tokeninfo");
        executor.getMap(INVALID_ACCESS_TOKEN);
    }

    @Test
    public void testFallbackToSecondEndpointValidToken() {
        FallbackTokenInfoRequestExecutor executor =
                new FallbackTokenInfoRequestExecutor("http://foobar:5212/tokeninfo, http://localhost:" + wireMockPort + "/tokeninfo");
        Map<String, Object> result = executor.getMap(SAMPLE_TOKEN_2);
        assertThat(result).hasSize(7);
    }

    @Test(expected = HttpClientErrorException.class)
    public void testFallbackToSecondEndpointInvalidToken() {
        FallbackTokenInfoRequestExecutor executor =
                new FallbackTokenInfoRequestExecutor("http://foobar:5212/tokeninfo, http://localhost:" + wireMockPort + "/tokeninfo");
        executor.getMap(INVALID_ACCESS_TOKEN);
    }

}
