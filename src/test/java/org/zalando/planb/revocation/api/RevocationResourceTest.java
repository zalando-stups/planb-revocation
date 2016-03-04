package org.zalando.planb.revocation.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;

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
     * Tests that when {@code GET}ing revocations without parameters, an HTTP {@code BAD_REQUEST} is returned.
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
     * Tests that when {@code GET}ing revocations with parameters other than {@code from}, an HTTP {@code BAD_REQUEST}
     * is returned.
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
     * Tests that when {@code GET}ing revocations with a parameter value with a different type, an HTTP
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
     * Tests that when {@code GET}ing revocations with a <code>null</code> parameter, an HTTP {@code BAD_REQUEST} is
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
}
