package org.zalando.planb.revocation.domain;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.common.base.Strings;

import lombok.Builder;
import lombok.Value;

/**
 * Represents a problem as specified by the <a href="https://tools.ietf.org/html/draft-ietf-appsawg-http-problem-03">
 * Problem Details draft</a>. Used to complement HTTP Status codes with additional contextual information.
 *
 * <p>A {@code Problem} has the following fields:</p>
 *
 * <ul>
 *   <li>{@code type} - URI set to "about:blank"</li>
 *   <li>{@code title} - from the reason phrase of the specified {@link HttpStatus}</li>
 *   <li>{@code status} - HTTP status code</li>
 *   <li>{@code detail} - detail provided by the message of the specified {@link Exception}</li>
 * </ul>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value
public class Problem {

    private final String type;

    private final String title;

    private final HttpStatus status;

    private final String detail;

    @JsonCreator
    @Builder
    private Problem(@JsonProperty("type") final String type,
            @JsonProperty("title") final String title,
            @JsonProperty("status") final HttpStatus status,
            @JsonProperty("detail") final String detail) {
        this.type = type == null || type.isEmpty() ? "about:blank" : type;
        this.title = Strings.nullToEmpty(title);
        this.status = status;
        this.detail = Strings.nullToEmpty(detail);
    }

    /**
     * Returns a {@code Problem} instance built with the provided {@link Exception} and {@link HttpStatus}.
     *
     * @param   e       an {@link Exception} related to the problem
     * @param   status  the {@link HttpStatus} related to the problem
     *
     * @return  an immutable {@code Problem} built from the specified parameters
     */
    public static Problem fromException(final Exception e, final HttpStatus status) {
        return fromMessage(e.getMessage(), status);
    }

    /**
     * Returns a {@code Problem} instance built with the provided {@code message} and {@link HttpStatus}.
     *
     * @param   message  a textual description of the problem
     * @param   status   the {@link HttpStatus} related to the problem
     *
     * @return  an immutable {@code Problem} built from the specified parameters
     */
    public static Problem fromMessage(final String message, final HttpStatus status) {
        return Problem.builder().status(status).title(status.getReasonPhrase()).detail(message).build();
    }

}
