package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.http.HttpStatus;

/**
 * A problem as specified by the <a href="https://tools.ietf.org/html/draft-ietf-appsawg-http-problem-03"> Problem
 * Details draft</a>.
 * <p>
 * <p>Used to complement HTTP Status codes with additional contextual information.</p>
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableProblem.class)
public abstract class Problem {

    /**
     * Returns a URI of this problem. Default is "about:blank".
     *
     * @return the URI of this Problem
     */
    @Value.Default
    public String type() {
        return "about:blank";
    }

    /**
     * Returns a short title of this Problem.
     *
     * @return the title of this Problem
     */
    public abstract String title();

    /**
     * Returns the HTTP status code of this Problem.
     *
     * @return the HTTP status code of this Problem
     */
    public abstract HttpStatus status();

    /**
     * Returns a detailed description of this Problem.
     *
     * @return a detailed description of this Problem
     */
    public abstract String detail();

    /**
     * Returns a {@code Problem} instance built with the provided {@link Exception} and {@link HttpStatus}.
     *
     * @param e      an {@link Exception} related to the problem
     * @param status the {@link HttpStatus} related to the problem
     * @return an immutable {@code Problem} built from the specified parameters
     */
    public static Problem fromException(Exception e, HttpStatus status) {
        return fromMessage(e.getMessage(), status);
    }

    /**
     * Returns a {@code Problem} instance built with the provided {@code message} and {@link HttpStatus}.
     *
     * @param message a textual description of the problem
     * @param status  the {@link HttpStatus} related to the problem
     * @return an immutable {@code Problem} built from the specified parameters
     */
    public static Problem fromMessage(String message, HttpStatus status) {
        return ImmutableProblem.builder().status(status).title(status.getReasonPhrase()).detail(message).build();
    }
}
