package org.zalando.planb.revocation.domain;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for problems.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class ProblemTest extends AbstractDomainTest {

    private final static String TYPE = "http://sourcesofinsight.com/4-types-of-problems/";

    private final static String TITLE = "Something tricky happened";

    private final static HttpStatus STATUS = HttpStatus.FORBIDDEN;

    private final static String DETAIL = "Dude, you have problems. Put yourself together!";

    private final static String SERIALIZED_PROBLEM = "{" +
            "\"type\":\"" + TYPE + "\"," +
            "\"title\":\"" + TITLE + "\"," +
            "\"status\":\"" + STATUS.getReasonPhrase().toUpperCase() + "\"," +
            "\"detail\":\"" + DETAIL + "\"}";

    private final static String SERIALIZED_PROBLEM_DEFAULTS = "{" +
            "\"title\":\"" + TITLE + "\"," +
            "\"status\":\"" + STATUS.getReasonPhrase().toUpperCase() + "\"," +
            "\"detail\":\"" + DETAIL + "\"}";

    /**
     * Tests that when instantiating a {@link Problem} all default values are set.
     */
    @Test
    public void testDefaultsAreSet() {
        Problem problem = ImmutableProblem.builder().title(TITLE).status(STATUS).detail(DETAIL).build();

        assertThat(problem.type()).isNotNull();
        assertThat(problem.type()).isEqualTo("about:blank");
    }

    /**
     * Tests JSON serialization of a {@link Problem} object.
     */
    @Test
    public void testJsonSerialization() throws IOException {
        Problem problem = ImmutableProblem.builder().type(TYPE).title(TITLE).status(STATUS).detail(DETAIL).build();

        String serialized = objectMapper.writeValueAsString(problem);
        assertThat(serialized).isEqualTo(SERIALIZED_PROBLEM);
    }

    /**
     * Tests JSON deserialization of a {@link Problem} object.
     */
    @Test
    public void testJsonDeserialization() throws IOException {

        Problem problem = objectMapper.readValue(SERIALIZED_PROBLEM, Problem.class);

        assertThat(problem.type()).isEqualTo(TYPE);
        assertThat(problem.title()).isEqualTo(TITLE);
        assertThat(problem.status()).isEqualTo(STATUS);
        assertThat(problem.detail()).isEqualTo(DETAIL);
    }

    /**
     * Tests JSON deserialization of a {@link Problem} object when the serialized string does not have values that
     * should be set by default.
     */
    @Test
    public void testJsonDeserializationSetsDefaults() throws IOException {

        Problem problem = objectMapper.readValue(SERIALIZED_PROBLEM_DEFAULTS, Problem.class);

        assertThat(problem.type()).isEqualTo("about:blank");
        assertThat(problem.title()).isEqualTo(TITLE);
        assertThat(problem.status()).isEqualTo(STATUS);
        assertThat(problem.detail()).isEqualTo(DETAIL);
    }

    /**
     * Tests that a problem is created from an {@link Exception} and {@link HttpStatus}.
     */
    @Test
    public void testProblemFromException() {
        Problem problem = Problem.fromException(new Exception(DETAIL), STATUS);

        assertThat(problem.type()).isEqualTo("about:blank");
        assertThat(problem.title()).isEqualTo(STATUS.getReasonPhrase());
        assertThat(problem.status()).isEqualTo(STATUS);
        assertThat(problem.detail()).isEqualTo(DETAIL);
    }

    /**
     * Tests that a problem is created from a message and {@link HttpStatus}.
     */
    @Test
    public void testProblemFromMessage() {
        Problem problem = Problem.fromMessage(DETAIL, STATUS);

        assertThat(problem.type()).isEqualTo("about:blank");
        assertThat(problem.title()).isEqualTo(STATUS.getReasonPhrase());
        assertThat(problem.status()).isEqualTo(STATUS);
        assertThat(problem.detail()).isEqualTo(DETAIL);
    }
}
