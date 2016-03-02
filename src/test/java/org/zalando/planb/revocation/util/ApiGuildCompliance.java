package org.zalando.planb.revocation.util;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.beans.factory.parsing.Problem;

import org.springframework.test.web.servlet.ResultActions;

/**
 * Utility methods to assert compliance of Zalando's API Guild directives.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class ApiGuildCompliance {

    /**
     * Asserts that the body contains a standard {@link Problem}.
     */
    public static void isStandardProblemResponse(final ResultActions result) throws Exception {
        result.andExpect(jsonPath("$.type").exists());
        result.andExpect(jsonPath("$.title").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.detail").exists());
    }
}
