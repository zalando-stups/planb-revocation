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
import org.zalando.planb.revocation.domain.ImmutableRevocationRequest;
import org.zalando.planb.revocation.domain.ImmutableRevokedClaimsData;
import org.zalando.planb.revocation.domain.ImmutableRevokedGlobal;
import org.zalando.planb.revocation.domain.ImmutableRevokedTokenData;
import org.zalando.planb.revocation.domain.RevocationRequest;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.RevokedData;
import org.zalando.planb.revocation.domain.RevokedGlobal;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

/**
 * @author jbellmann
 */
public abstract class AbstractSpringTest {

    public static final String VALID_ACCESS_TOKEN = "Bearer 123456789";
    public static final String INVALID_ACCESS_TOKEN = "Bearer 987654321";
    public static final String INSUFFICIENT_SCOPES_ACCESS_TOKEN = "Bearer 123456";
    public static final String SERVER_ERROR_ACCESS_TOKEN = "Bearer 500";

    public static final String TOKENINFO_RESPONSE = "{\n" + "    \"uid\": \"testapp\",\n" + "    \"scope\": [\n"
            + "        \"uid\",\n" + "        \"token.revoke\"\n" + "    ],\n" + "    \"hello\": \"World\",\n"
            + "    \"expires_in\": 99999,\n" + "    \"token_type\": \"Bearer\",\n"
            + "    \"access_token\": \"987654321\",\n" + "    \"realm\": \"/services\"\n" + "}";

    public static final String TOKENINFO_RESPONSE_INSUFFICIENT_SCOPES = "{\n" + "    \"uid\": \"testapp\",\n"
            + "    \"scope\": [\n" + "        \"uid\"\n" + "    ],\n" + "    \"expires_in\": 99999,\n"
            + "    \"token_type\": \"Bearer\",\n" + "    \"access_token\": \"987654321\",\n"
            + "    \"realm\": \"/services\"\n" + "}";

    public static final String EXPIRED_ACCESS_TOKEN_RESPONSE = "{\n" + "    \"error\": \"invalid_request\",\n"
            + "    \"error_description\": \"Access Token not valid\"\n" + "}";

    public static final String SAMPLE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
            + ".eyJzdWIiOiIxIiwibmFtZSI6InJyZWlzIiwiYWRtaW4iOnRydWV9.UlZhyvrY9e7tRU88l8sfRb37oWGiL2t4insnO9Nsn1c";

    @Rule
    public WireMockRule wireMock = new WireMockRule(Integer.valueOf(System.getProperty("wiremock.port", "10080")));

    @Rule
    public SpringMethodRule methodRule = new SpringMethodRule();

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Before
    public void setupMockTokenInfo() {
        stubFor(get(urlEqualTo("/tokeninfo")).withHeader(HttpHeaders.AUTHORIZATION, equalTo(VALID_ACCESS_TOKEN))
                .willReturn(
                        aResponse().withStatus(HttpStatus.OK.value()).withHeader(ContentTypeHeader.KEY,
                                MediaType.APPLICATION_JSON_VALUE).withBody(TOKENINFO_RESPONSE)));

        stubFor(get(urlEqualTo("/tokeninfo")).withHeader(HttpHeaders.AUTHORIZATION, equalTo(INVALID_ACCESS_TOKEN))
                .willReturn(
                        aResponse().withStatus(HttpStatus.BAD_REQUEST.value()).withHeader(ContentTypeHeader.KEY,
                                MediaType.APPLICATION_JSON_VALUE).withBody(EXPIRED_ACCESS_TOKEN_RESPONSE)));

        stubFor(get(urlEqualTo("/tokeninfo")).withHeader(HttpHeaders.AUTHORIZATION,
                equalTo(INSUFFICIENT_SCOPES_ACCESS_TOKEN)).willReturn(
                aResponse().withStatus(HttpStatus.OK.value()).withHeader(ContentTypeHeader.KEY,
                        MediaType.APPLICATION_JSON_VALUE).withBody(TOKENINFO_RESPONSE_INSUFFICIENT_SCOPES)));

        stubFor(get(urlEqualTo("/tokeninfo")).withHeader(HttpHeaders.AUTHORIZATION,
                equalTo(SERVER_ERROR_ACCESS_TOKEN)).willReturn(
                aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }

    // Some utility methods
    public static RevocationRequest generateRevocation(final RevocationType type) {

        RevokedData data = null;
        switch (type) {

            case TOKEN:
                data = ImmutableRevokedTokenData.builder().token(SAMPLE_TOKEN).build();
                break;

            case CLAIM:
                data = ImmutableRevokedClaimsData.builder().putClaims("uid", "rreis").putClaims("sub", "abcd").build();
                break;

            case GLOBAL:
                data = ImmutableRevokedGlobal.builder().build();
                break;
        }

        return ImmutableRevocationRequest.builder()
                .type(type)
                .data(data)
                .build();
    }
}
