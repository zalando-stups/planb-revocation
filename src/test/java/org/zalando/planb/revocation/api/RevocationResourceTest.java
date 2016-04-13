package org.zalando.planb.revocation.api;

import exclude.from.componentscan.NoopRevocationAuthorizationConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zalando.planb.revocation.AbstractSpringTest;
import org.zalando.planb.revocation.Main;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.config.properties.RevocationProperties;
import org.zalando.planb.revocation.domain.Problem;
import org.zalando.planb.revocation.util.ApiGuildCompliance;
import org.zalando.planb.revocation.util.InstantTimestamp;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for endpoint {@code /revocations}.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@SpringApplicationConfiguration(classes = {Main.class, NoopRevocationAuthorizationConfig.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("test")
public class RevocationResourceTest extends AbstractSpringTest {

    private static final String GLOBAL_REVOCATION = "{ " +
            "\"type\": \"GLOBAL\", " +
            "\"data\": {\"issued_before\":1459939746} }";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RevocationProperties revocationProperties;

    @Autowired
    private CassandraProperties cassandraProperties;

    private MockMvc mvc;

    @Before
    public void setupMockMcv() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * Tests that when {@code GET}ing revocations without parameters, a HTTP {@code BAD_REQUEST} is returned.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testBadRequestWhenEmptyParamsOnGet() throws Exception {
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/revocations").accept(
                    MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());

        ApiGuildCompliance.isStandardProblem(result);
    }

    /**
     * Tests that when {@code GET}ing revocations with parameters other than {@code from}, a HTTP {@code BAD_REQUEST} is
     * returned.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testBadRequestWhenOtherParamsOnGet() throws Exception {
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/revocations?to=").accept(
                    MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());

        ApiGuildCompliance.isStandardProblem(result);
    }

    /**
     * Tests that when {@code GET}ing revocations with a parameter value with a different type, a HTTP
     * {@code BAD_REQUEST} is returned.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testBadRequestWhenTypeMismatchOnGet() throws Exception {
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/revocations?from=ilwhefouweh").accept(
                    MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());

        ApiGuildCompliance.isStandardProblem(result);
    }

    /**
     * Tests that when {@code GET}ing revocations with a <code>null</code> parameter, a HTTP {@code BAD_REQUEST} is
     * returned.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testBadRequestWhenNullParamOnGet() throws Exception {
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/revocations?from=").accept(
                    MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());

        ApiGuildCompliance.isStandardProblem(result);
    }

    /**
     * Tests that when {@code POST}ing revocations with a non-JSON body, a HTTP {@code BAD_REQUEST} is returned.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testBadRequestWhenNoJsonBodyInPost() throws Exception {
        ResultActions result = mvc.perform(MockMvcRequestBuilders.post("/revocations").contentType(
                    MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).content(
                    "<json>Sure I am!</json>"));

        result.andExpect(status().isBadRequest());

        ApiGuildCompliance.isStandardProblem(result);
    }

    /**
     * Tests that when {@code POST}ing revocations with an unexpected JSON body, a HTTP {@code BAD_REQUEST} is returned.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testBadRequestWhenUnexpectedJsonBodyInPost() throws Exception {
        String jsonObject = "{ space: [ { odyssey: [ \"2001\" ] } ] }";
        ResultActions result = mvc.perform(MockMvcRequestBuilders.post("/revocations").contentType(
                MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).content(
                jsonObject));

        result.andExpect(status().isBadRequest());

        ApiGuildCompliance.isStandardProblem(result);
    }

    /**
     * Tests that when POSTing a {@code GLOBAL} revocation, returns {@code HTTP FORBIDDEN} and a problem description.
     */
    @Test
    public void testForbiddenWhenPostingGlobal() throws Exception {
        ResultActions result = mvc.perform(MockMvcRequestBuilders.post("/revocations").contentType(
                MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).content(
                GLOBAL_REVOCATION));

        result.andExpect(status().isForbidden());
        ApiGuildCompliance.isStandardProblem(result);
    }

    /**
     * Tests that when {@code POST}ing revocations with a future {@code issued_before} field, a HTTP {@code BAD_REQUEST}
     * is returned.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testBadRequestWhenPostingFutureRevocation() throws Exception {
        String claimRevocation = "{ \"type\": \"CLAIM\", \"data\": {\"claims\":{\"uid\":\"3035729288\"}," +
                "\"issued_before\":" + (InstantTimestamp.FIVE_MINUTES_AFTER.seconds() + revocationProperties
                .getTimestampThreshold()) + "} }";

        ResultActions result = mvc.perform(MockMvcRequestBuilders.post("/revocations").contentType(
                MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).content(
                claimRevocation));

        result.andExpect(status().isBadRequest());

        ApiGuildCompliance.isStandardProblem(result);
    }

    /**
     * Tests that when {@code POST}ing revocations with an ancient {@code issued_before} field, a HTTP {@code BAD_REQUEST}
     * is returned.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.</p>
     */
    @Test
    public void testBadRequestWhenPostingAncientRevocation() throws Exception {
        String claimRevocation = "{ \"type\": \"CLAIM\", \"data\": {\"claims\":{\"uid\":\"3035729288\"}," +
                "\"issued_before\":" + (InstantTimestamp.ONE_HOUR_AGO.seconds() - cassandraProperties.getMaxTimeDelta() ) + "} }";

        ResultActions result = mvc.perform(MockMvcRequestBuilders.post("/revocations").contentType(
                MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).content(
                claimRevocation));

        result.andExpect(status().isBadRequest());

        ApiGuildCompliance.isStandardProblem(result);
    }

    /**
     * Tests that when {@code POST}ing revocations with a future {@code issued_before} field (but behind the
     * threshold limit), a HTTP {@code CREATED} is returned.
     */
    @Test
    public void testOkWhenPostingRevocationBehindTimeThreshold() throws Exception {
        String claimRevocation = "{ \"type\": \"CLAIM\", \"data\": {\"claims\":{\"uid\":\"3035729288\"}," +
                "\"issued_before\":" + (InstantTimestamp.NOW.seconds() + revocationProperties
                .getTimestampThreshold() - 1) + "} }";

        ResultActions result = mvc.perform(MockMvcRequestBuilders.post("/revocations").contentType(
                MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, VALID_ACCESS_TOKEN).content(
                claimRevocation));

        result.andExpect(status().isCreated());
    }
}
