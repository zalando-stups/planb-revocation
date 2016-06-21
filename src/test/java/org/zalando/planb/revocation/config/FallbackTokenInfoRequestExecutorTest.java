package org.zalando.planb.revocation.config;

import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.zalando.planb.revocation.AbstractOAuthTest;

import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class FallbackTokenInfoRequestExecutorTest extends AbstractOAuthTest {

    private static final String INVALID_TOKEN_INFO_URI = "http://foobar:5212/tokeninfo";
    private static final Integer FIRST_TOKEN_INFO_PORT = 10081;
    private static final String FIRST_TOKEN_INFO_URI = "http://localhost:" + FIRST_TOKEN_INFO_PORT + "/tokeninfo";
    private static final String SECOND_TOKEN_INFO_URI =
            "http://localhost:" + Integer.valueOf(System.getProperty("wiremock.port", "10080")) + "/tokeninfo";

    @Rule
    public WireMockRule secondServerMock = new WireMockRule(FIRST_TOKEN_INFO_PORT);

    @Test
    public void testSingleEndpointValidToken() {
        FallbackTokenInfoRequestExecutor executor =
                new FallbackTokenInfoRequestExecutor(SECOND_TOKEN_INFO_URI);
        Map<String, Object> result = executor.getMap(SAMPLE_TOKEN_2);
        assertThat(result).hasSize(7);
    }

    @Test(expected = HttpClientErrorException.class)
    public void testSingleEndpointNotFound() {
        FallbackTokenInfoRequestExecutor executor =
                new FallbackTokenInfoRequestExecutor(SECOND_TOKEN_INFO_URI);
        executor.getMap(UUID.randomUUID().toString());
    }

    @Test
    public void testSingleEndpointInvalid() {
        FallbackTokenInfoRequestExecutor executor =
                new FallbackTokenInfoRequestExecutor(SECOND_TOKEN_INFO_URI);
        Map<String, Object> result = executor.getMap(INVALID_TOKEN);
        assertThat(result).containsKey("error");
    }

    @Test
    public void testFallbackToSecondEndpointValidToken() {
        FallbackTokenInfoRequestExecutor executor =
                new FallbackTokenInfoRequestExecutor(INVALID_TOKEN_INFO_URI + "," + SECOND_TOKEN_INFO_URI);
        Map<String, Object> result = executor.getMap(SAMPLE_TOKEN_2);
        assertThat(result).hasSize(7);
    }

    @Test
    public void testFallbackFirstInvalidSecondValid() {
        secondServerMock.stubFor(get(urlEqualTo("/tokeninfo"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo(VALID_ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(EXPIRED_ACCESS_TOKEN_RESPONSE)));

        FallbackTokenInfoRequestExecutor executor =
                new FallbackTokenInfoRequestExecutor(FIRST_TOKEN_INFO_URI + "," + SECOND_TOKEN_INFO_URI);
        Map<String, Object> result = executor.getMap(SAMPLE_TOKEN_2);
        assertThat(result).hasSize(7);
    }

    @Test(expected = HttpClientErrorException.class)
    public void testFallbackToSecondEndpointNotFound() {
        FallbackTokenInfoRequestExecutor executor =
                new FallbackTokenInfoRequestExecutor(INVALID_TOKEN_INFO_URI + "," + SECOND_TOKEN_INFO_URI);
        executor.getMap(INVALID_ACCESS_TOKEN);
    }

    @Test
    public void testFallbackToSecondEndpointInvalidToken() {
        FallbackTokenInfoRequestExecutor executor =
                new FallbackTokenInfoRequestExecutor(INVALID_TOKEN_INFO_URI + "," + SECOND_TOKEN_INFO_URI);
        Map<String, Object> result = executor.getMap(INVALID_TOKEN);
        assertThat(result).containsKey("error");
    }
}
