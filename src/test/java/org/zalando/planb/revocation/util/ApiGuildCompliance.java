package org.zalando.planb.revocation.util;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.IOException;

import org.springframework.test.web.servlet.ResultActions;

import org.zalando.planb.revocation.domain.Problem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Utility methods to assert compliance of Zalando's API Guild directives.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class ApiGuildCompliance {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    }

    /**
     * Asserts that the body contains a standard {@link Problem}.
     */
    public static void isStandardProblem(final ResultActions result) throws Exception {
        result.andExpect(jsonPath("$.type").exists());
        result.andExpect(jsonPath("$.title").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.detail").exists());
    }

    /**
     * Returns {@code true} if the specified {@code json} is a standard {@link Problem}.
     *
     * @param   json  a JSON String with a Problem
     *
     * @return  {@code true} if the specified {@code json} is a valid {@link Problem}
     */
    public static boolean isStandardProblem(final String json) {
        if (json == null) {
            return false;
        }

        Problem problem = null;
        try {
            problem = MAPPER.readValue(json, Problem.class);
        } catch (IOException e) {
            return false;
        }

        // All fields must not be null
        if (problem.getType() == null) {
            return false;
        }

        if (problem.getTitle() == null) {
            return false;
        }

        if (problem.getStatus() == null) {
            return false;
        }

        if (problem.getDetail() == null) {
            return false;
        }

        return true;
    }
}
