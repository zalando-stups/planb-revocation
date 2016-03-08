package org.zalando.planb.revocation.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.zalando.planb.revocation.domain.Problem;
import org.zalando.planb.revocation.util.ApiGuildCompliance;

/**
 * Unit tests for endpoint {@code /revocations}.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("test")
public class RevocationResourceTest extends AbstractSpringTest {

    @Autowired
    private WebApplicationContext context;

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
}
