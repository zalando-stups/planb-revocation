package org.zalando.planb.revocation.api;

import org.slf4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zalando.planb.revocation.api.exception.AncientRevocationException;
import org.zalando.planb.revocation.api.exception.FutureRevocationException;
import org.zalando.planb.revocation.api.exception.RevocationUnauthorizedException;
import org.zalando.planb.revocation.api.exception.SerializationException;
import org.zalando.planb.revocation.domain.Problem;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Provides global exception handling for all controllers.
 *
 * @author <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@ControllerAdvice
@RequestMapping(produces = "application/x.problem+json")
public class ExceptionsResource {

    private final Logger log = getLogger(getClass());

    @ExceptionHandler(RevocationUnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Problem revocationUnauthorized(final RevocationUnauthorizedException e) {
        log.debug("Revocation request was unauthorized", e);

        return Problem.fromException(e, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles revocations with an {@code issued_before} timestamp set in the future.
     *
     * @param e the exception triggering the error
     * @return a {@link Problem} with the error information from the exception.
     */
    @ExceptionHandler(FutureRevocationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Problem futureRevocation(final FutureRevocationException e) {
        return Problem.fromException(e, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles revocations with an {@code issued_before} timestamp set in the ancient past.
     * {@code issued_before} < now() - {@code cassandra.maxTimeDelta}
     *
     * @param e the exception triggering the error
     * @return a {@link Problem} with the error information from the exception.
     */
    @ExceptionHandler(AncientRevocationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Problem ancientRevocation(final AncientRevocationException e) {
        return Problem.fromException(e, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles missing parameters in requests.
     *
     * @param e the exception triggering the error
     * @return a {@link Problem} with the error information from the exception.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Problem missingParameters(final MissingServletRequestParameterException e) {
        return Problem.fromException(e, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles illegal arguments in parameters.
     * <p>
     * <p>Used for example when a {@code null} value is assigned to a parameter that shouldn't be {@code null}</p>
     *
     * @param e the exception triggering the error
     * @return a {@link Problem} with the error information from the exception.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Problem illegalArgument(final IllegalArgumentException e) {
        return Problem.fromException(e, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles type mismatch errors in parameter values.
     *
     * @param e the exception triggering the error
     * @return a {@link Problem} with the type mismatch error information.
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Problem typeMismatch(final TypeMismatchException e) {
        return Problem.fromMessage("Type mismatch in parameter value.", HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles messages which are not readable because they are not valid JSON, or have an invalid JSON structure.
     *
     * @param e the exception triggering the error
     * @return a {@link Problem} with the message not readable information.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Problem messageNotReadable(final HttpMessageNotReadableException e) {
        return Problem.fromMessage("Invalid JSON, or invalid JSON structure.", HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles access denied requests.
     *
     * @param e the exception triggering the error
     * @return a {@link Problem} reason for denying the access
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Problem accessDenied(final AccessDeniedException e) {
        return Problem.fromException(e, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles all exceptions related with serializing revocation data to the store.
     *
     * @param e the exception triggering the error
     * @return a {@link Problem} with the unexpected error information.
     */
    @ExceptionHandler(SerializationException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Problem serializationException(final SerializationException e) {
        log.error("A serialization exception occurred", e);

        return Problem.fromException(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all remaining exceptions not covered by the other handlers.
     * <p>
     * <p>When an exception that is not covered by this {@link ControllerAdvice}, returns an HTTP 500 Internal Server
     * Error with a generic {@link Problem} informing the unexpected error.</p>
     *
     * @param e the exception triggering the error
     * @return a {@link Problem} with the unexpected error information.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Problem handleGenericException(final Exception e) {
        log.error("An unexpected error occurred", e);

        return Problem.fromMessage("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}