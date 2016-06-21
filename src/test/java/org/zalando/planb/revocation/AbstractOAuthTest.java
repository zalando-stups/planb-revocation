package org.zalando.planb.revocation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.zalando.planb.revocation.domain.*;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * @author jbellmann
 */
public class AbstractOAuthTest {
    public static final String SAMPLE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
            + ".eyJzdWIiOiIxIiwibmFtZSI6InJyZWlzIiwiYWRtaW4iOnRydWV9.UlZhyvrY9e7tRU88l8sfRb37oWGiL2t4insnO9Nsn1c";
    public static final String SAMPLE_TOKEN_2 = "eyJraWQiOiJ0ZXN0a2V5LWVzMjU2IiwiYWxnIjoiRVMyNTYifQ" +
            ".eyJzdWIiOiJ0ZXN0MCIsInNjb3BlIjpbInVpZCIsImNuIl0sImlzcyI6IkIiLCJyZWFsbSI6Ii9zZXJ2aWNlcyIsImV4cCI6MTQ1OTk3MzMyOSwiaWF0IjoxNDU5OTQ0NTI5fQ" +
            ".Vo8_jbqCET31ej1iLAlcQFc2FzArzQrQwDY3c34keKhpJoDQoHVOX-pqjiM5J_Tp0p13HNZbB3-O4o0U2d2LzA";
    public static final String VALID_ACCESS_TOKEN = "Bearer " + SAMPLE_TOKEN_2;
    public static final String INVALID_TOKEN = "987654321";
    public static final String INVALID_ACCESS_TOKEN = "Bearer " + INVALID_TOKEN;
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

    @Autowired(required = false)
    private ObjectMapper objectMapper = new ObjectMapper();

    @Rule
    public WireMockRule wireMock = new WireMockRule(Integer.valueOf(System.getProperty("wiremock.port", "10080")));

    @Before
    public void setUpTest() {
        prepareTokenInfoMock();
        prepareRestTemplate();
        configureJsonPath();
    }

    /*
     * Do not use default JsonSmart provider, instead use Jackson.
     */
    private void configureJsonPath() {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    private void prepareTokenInfoMock() {
        wireMock.stubFor(get(urlEqualTo("/tokeninfo")).withHeader(HttpHeaders.AUTHORIZATION, equalTo(VALID_ACCESS_TOKEN))
                .willReturn(
                        aResponse().withStatus(HttpStatus.OK.value()).withHeader(ContentTypeHeader.KEY,
                                MediaType.APPLICATION_JSON_VALUE).withBody(TOKENINFO_RESPONSE)));

        wireMock.stubFor(get(urlEqualTo("/tokeninfo")).withHeader(HttpHeaders.AUTHORIZATION, equalTo(INVALID_ACCESS_TOKEN))
                .willReturn(
                        aResponse().withStatus(HttpStatus.BAD_REQUEST.value()).withHeader(ContentTypeHeader.KEY,
                                MediaType.APPLICATION_JSON_VALUE).withBody(EXPIRED_ACCESS_TOKEN_RESPONSE)));

        wireMock.stubFor(get(urlEqualTo("/tokeninfo")).withHeader(HttpHeaders.AUTHORIZATION,
                equalTo(INSUFFICIENT_SCOPES_ACCESS_TOKEN)).willReturn(
                aResponse().withStatus(HttpStatus.OK.value()).withHeader(ContentTypeHeader.KEY,
                        MediaType.APPLICATION_JSON_VALUE).withBody(TOKENINFO_RESPONSE_INSUFFICIENT_SCOPES)));

        wireMock.stubFor(get(urlEqualTo("/tokeninfo")).withHeader(HttpHeaders.AUTHORIZATION,
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
        return ImmutableRevocationRequest.builder()
                .type(RevocationType.CLAIM)
                .data(ImmutableRevokedClaimsData.builder().putAllClaims(claims).build())
                .build();
    }

    public static RevocationRequest generateRevocation(final RevocationType type) {

        RevokedData data = null;
        switch (type) {

            case TOKEN:
                data = ImmutableRevokedTokenData.builder().token(SAMPLE_TOKEN).build();
                break;

            case CLAIM:
                return generateClaimBasedRevocation(ImmutableMap.of("uid", "rreis", "sub", "abcd"));

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
