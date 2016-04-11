package org.zalando.planb.revocation.api.exception;

/**
 * Thrown when a client tries to post a revocation with an {@code issued_before} timestamp in the ancient past.
 *
 */
public class AncientRevocationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "'issued_before' cannot be set in the ancient past.";

    public AncientRevocationException() {
        super(MESSAGE);
    }
}
