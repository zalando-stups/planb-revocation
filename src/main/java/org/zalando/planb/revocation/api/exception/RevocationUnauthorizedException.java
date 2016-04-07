package org.zalando.planb.revocation.api.exception;

import org.zalando.planb.revocation.domain.AuthorizationRule;

import java.text.MessageFormat;

public class RevocationUnauthorizedException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "The requested revocation with claims {0} is not authorized";

    public RevocationUnauthorizedException(AuthorizationRule rule) {
        super(MessageFormat.format(MESSAGE, rule.allowedRevocationClaims()));
    }
}
