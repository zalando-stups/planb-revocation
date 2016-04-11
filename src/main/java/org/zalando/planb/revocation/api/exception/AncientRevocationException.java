package org.zalando.planb.revocation.api.exception;

/**
 * Thrown when a client tries to post a revocation with an {@code issued_before} timestamp too old.
 * Value must be greater than {@code UnixTimestamp#now()} - {@code cassandra.maxTimeDelta}
 *
 */
public class AncientRevocationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "'issued_before' is too old.";

    public AncientRevocationException() {
        super(MESSAGE);
    }
}
