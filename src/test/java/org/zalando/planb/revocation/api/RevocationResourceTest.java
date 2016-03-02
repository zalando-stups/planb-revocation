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
 * Utility methods to assert compliance of Zalando's API Guild directives.
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
     * Tests that when <code>GET</code>ing revocations without parameters, an HTTP <code>BAD_REQUEST</code> is returned.
     *
     * <p>Furthermore asserts that a standard {@link Problem} is returned.
     */
    @Test
    public void testBadRequestWhenEmptyParamsOnGet() throws Exception {
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/revocations").accept(
                    MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest());

        ApiGuildCompliance.isStandardProblemResponse(result);
    }
}
