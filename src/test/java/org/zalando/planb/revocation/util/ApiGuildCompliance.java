package org.zalando.planb.revocation.util;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.ResultActions;

/**
 * Utility methods to assert compliance of Zalando's API Guild directives.
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
public class ApiGuildCompliance {

    /**
     * Verifies that the body contains a standard Problem Response .
     *
     * @return
     */
    public static void isStandardProblemResponse(final ResultActions result) throws Exception {
        result.andExpect(jsonPath("$.type").exists());
        result.andExpect(jsonPath("$.title").exists());

        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.status").value(status().toString()));

        result.andExpect(jsonPath("$.detail").exists());

    }
}
