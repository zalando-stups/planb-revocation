package org.zalando.planb.revocation.api.exception;

/**
 * Thrown when a client tries to post a revocation with an {@code issued_before} timestamp in the future.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class FutureRevocationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "'issued_before' cannot be set in the future.";

    public FutureRevocationException() {
        super(MESSAGE);
    }
}
