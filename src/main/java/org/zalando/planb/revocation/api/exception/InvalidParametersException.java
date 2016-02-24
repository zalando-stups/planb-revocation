package org.zalando.planb.revocation.api.exception;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
public class InvalidParametersException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Invalid or nonexistent parameters";

    public InvalidParametersException() {
        super(MESSAGE);
    }
}
