package org.zalando.planb.revocation.api;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.zalando.planb.revocation.domain.ProblemResponse;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@ControllerAdvice
@RequestMapping(produces = "application/x.problem+json")
public class ExceptionsResource {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemResponse missingParameters(final MissingServletRequestParameterException e) {
        return ProblemResponse.fromExceptionWithStatus(e, HttpStatus.BAD_REQUEST);
    }
}
