package org.zalando.planb.revocation.domain;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.common.base.Strings;

import lombok.Builder;
import lombok.Value;

/**
 * Represents a problem as specified by the <a href="https://tools.ietf.org/html/draft-ietf-appsawg-http-problem-03">
 * Problem Details draft</a>.
 *
 * <p>Used to complement HTTP Status codes with additional contextual information.</p>
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Value
public class ProblemResponse {

    private final String type;

    private final String title;

    private final HttpStatus status;

    private final String detail;

    @JsonCreator
    @Builder
    private ProblemResponse(@JsonProperty("type") final String type,
            @JsonProperty("title") final String title,
            @JsonProperty("status") final HttpStatus status,
            @JsonProperty("detail") final String detail) {
        this.type = type == null || type.isEmpty() ? "about:blank" : type;
        this.title = Strings.nullToEmpty(title);
        this.status = status;
        this.detail = Strings.nullToEmpty(detail);
    }

    /**
     * Returns a {@code ProblemResponse} instance built with the provided {@code Exception} and {@code HttpStatus}.
     *
     * <p>The resulting object will have the following fields:</p>
     *
     * <ul>
     *   <li>{@code type} - URI set to "about:blank"</li>
     *   <li>{@code title} - from the reason phrase of the specified {@code HttpStatus}</li>
     *   <li>{@code status} - HTTP status code</li>
     *   <li>{@code detail} - detail provided by the message of the specified {@code Exception}</li>
     * </ul>
     *
     * @param   e       an {@code Exception} related to the problem
     * @param   status  the HTTP status code related to the problem
     *
     * @return  an immutable {@code ProblemResponse} built from the specified parameters
     */
    public static ProblemResponse fromExceptionWithStatus(final Exception e, final HttpStatus status) {
        return ProblemResponse.builder().status(status).title(status.getReasonPhrase()).detail(e.getMessage()).build();
    }
}
