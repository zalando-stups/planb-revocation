package org.zalando.planb.revocation;

import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.TokenRevocationData;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

/**
 * @author jbellmann
 */
public abstract class AbstractSpringTest {

    static final String VALID_ACCESS_TOKEN = "Bearer 123456789";
    static final String INVALID_ACCESS_TOKEN = "Bearer 987654321";
    static final String INSUFFICIENT_SCOPES_ACCESS_TOKEN = "Bearer 987654321";


    private static final String TOKENINFO_RESPONSE = "{\n" +
            "    \"uid\": \"testapp\",\n" +
            "    \"scope\": [\n" +
            "        \"uid\",\n" +
            "        \"token.revoke\"\n" +
            "    ],\n" +
            "    \"hello\": \"World\",\n" +
            "    \"expires_in\": 99999,\n" +
            "    \"token_type\": \"Bearer\",\n" +
            "    \"access_token\": \"987654321\",\n" +
            "    \"realm\": \"/services\"\n" +
            "}";

    private static final String TOKENINFO_RESPONSE_INSUFFICIENT_SCOPES = "{\n" +
            "    \"uid\": \"testapp\",\n" +
            "    \"scope\": [\n" +
            "        \"uid\"\n" +
            "    ],\n" +
            "    \"expires_in\": 99999,\n" +
            "    \"token_type\": \"Bearer\",\n" +
            "    \"access_token\": \"987654321\",\n" +
            "    \"realm\": \"/services\"\n" +
            "}";

    private static final String EXPIRED_ACCESS_TOKEN_RESPONSE = "{\n" +
            "    \"error\": \"invalid_request\",\n" +
            "    \"error_description\": \"Access Token not valid\"\n" +
            "}";

    @Rule
    public WireMockRule wireMock = new WireMockRule(Integer.valueOf(System.getProperty("wiremock.port", "10080")));

    @Rule
    public SpringMethodRule methodRule = new SpringMethodRule();

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Before
    public void setupMockTokenInfo() {
        stubFor(get(urlEqualTo("/tokeninfo"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo(VALID_ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(TOKENINFO_RESPONSE)));

        stubFor(get(urlEqualTo("/tokeninfo"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo(INVALID_ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(EXPIRED_ACCESS_TOKEN_RESPONSE)));

        stubFor(get(urlEqualTo("/tokeninfo"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo(INSUFFICIENT_SCOPES_ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(TOKENINFO_RESPONSE_INSUFFICIENT_SCOPES)));

    }

    // Some utility methods
    static Revocation generateRevocation(RevocationType type) {

        Revocation generated = new Revocation();

        RevocationData revocationData = null;
        switch(type) {
            case TOKEN:
                revocationData = new TokenRevocationData();
                ((TokenRevocationData) revocationData).setTokenHash(UUID.randomUUID().toString());
                ((TokenRevocationData) revocationData).setRevokedAt(System.currentTimeMillis());
                break;
        }

        generated.setType(RevocationType.TOKEN);
        generated.setRevokedAt(System.currentTimeMillis());
        generated.setData(revocationData);

        return generated;
    }
}
