package org.zalando.planb.revocation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.web.client.RestTemplate;
import org.zalando.planb.revocation.config.PlanBRevocationConfig;
import org.zalando.planb.revocation.domain.ImmutableRevokedClaimsData;
import org.zalando.planb.revocation.domain.ImmutableRevokedGlobal;
import org.zalando.planb.revocation.domain.ImmutableRevokedTokenData;
import org.zalando.planb.revocation.domain.RevocationRequest;
import org.zalando.planb.revocation.domain.RevocationType;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

/**
 * @author  jbellmann
 */
@ContextConfiguration(classes = PlanBRevocationConfig.class)
public abstract class AbstractSpringTest {

    public static final String SAMPLE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
            + ".eyJzdWIiOiIxIiwibmFtZSI6InJyZWlzIiwiYWRtaW4iOnRydWV9.UlZhyvrY9e7tRU88l8sfRb37oWGiL2t4insnO9Nsn1c";
    public static final String SAMPLE_TOKEN_2 = "eyJraWQiOiJ0ZXN0a2V5LWVzMjU2IiwiYWxnIjoiRVMyNTYifQ.eyJzdWIiOiJ0ZXN0MCIsInNjb3BlIjpbInVpZCIsImNuIl0sImlzcyI6IkIiLCJyZWFsbSI6Ii9zZXJ2aWNlcyIsImV4cCI6MTQ1OTk3MzMyOSwiaWF0IjoxNDU5OTQ0NTI5fQ.Vo8_jbqCET31ej1iLAlcQFc2FzArzQrQwDY3c34keKhpJoDQoHVOX-pqjiM5J_Tp0p13HNZbB3-O4o0U2d2LzA";
    public static final String VALID_ACCESS_TOKEN = "Bearer " + SAMPLE_TOKEN_2;
    public static final String INVALID_ACCESS_TOKEN = "Bearer 987654321";
    public static final String INSUFFICIENT_SCOPES_ACCESS_TOKEN = "Bearer 123456";
    public static final String SERVER_ERROR_ACCESS_TOKEN = "Bearer 500";

    public static final String TOKENINFO_RESPONSE = "{\n" + "    \"uid\": \"testapp\",\n" + "    \"scope\": [\n"
            + "        \"uid\",\n" + "        \"token.revoke\"\n" + "    ],\n" + "    \"hello\": \"World\",\n"
            + "    \"expires_in\": 99999,\n" + "    \"token_type\": \"Bearer\",\n"
            + "    \"access_token\": \"" + SAMPLE_TOKEN_2 + "\",\n" + "    \"realm\": \"/services\"\n" + "}";

    public static final String TOKENINFO_RESPONSE_INSUFFICIENT_SCOPES = "{\n" + "    \"uid\": \"testapp\",\n"
            + "    \"scope\": [\n" + "        \"uid\"\n" + "    ],\n" + "    \"expires_in\": 99999,\n"
            + "    \"token_type\": \"Bearer\",\n" + "    \"access_token\": \"987654321\",\n"
            + "    \"realm\": \"/services\"\n" + "}";

    public static final String EXPIRED_ACCESS_TOKEN_RESPONSE = "{\n" + "    \"error\": \"invalid_request\",\n"
            + "    \"error_description\": \"Access Token not valid\"\n" + "}";


    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Rule
    public WireMockRule wireMock = new WireMockRule(Integer.valueOf(System.getProperty("wiremock.port", "10080")));

    @Rule
    public SpringMethodRule methodRule = new SpringMethodRule();

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Before
    public void setUpTest() {
        prepareTokenInfoMock();
        prepareRestTemplate();
    }

    private void prepareTokenInfoMock() {
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

    private void prepareRestTemplate() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        /*
         * We need to replace the default object mapper with ours object mapper so it reads lower case with
         * underscores correctly.
         */
        for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
            if (restTemplate.getMessageConverters().get(i) instanceof MappingJackson2HttpMessageConverter) {
                restTemplate.getMessageConverters().set(i, new MappingJackson2HttpMessageConverter(objectMapper));
            }
        }
    }

    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    // Some utility methods
    public static RevocationRequest generateClaimBasedRevocation(Map<String, String> claims) {
        RevocationRequest generated = new RevocationRequest();
        generated.setType(RevocationType.CLAIM);
        generated.setData(ImmutableRevokedClaimsData.builder().putAllClaims(claims).build());
        return generated;
    }

    public static RevocationRequest generateRevocation(final RevocationType type) {

        RevocationRequest generated = new RevocationRequest();
        generated.setType(type);

        switch (type) {

            case TOKEN:
                generated.setData(ImmutableRevokedTokenData.builder().token(SAMPLE_TOKEN).build());
                break;

            case CLAIM :
                return generateClaimBasedRevocation(ImmutableMap.of("uid", "rreis", "sub", "abcd"));

            case GLOBAL:
                generated.setData(ImmutableRevokedGlobal.builder().build());
                break;
        }

        return generated;
    }
}
