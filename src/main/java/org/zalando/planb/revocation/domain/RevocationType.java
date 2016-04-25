package org.zalando.planb.revocation.domain;

/**
 * Available revocation types.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public enum RevocationType {

    /**
     * Revocation of a single token.
     */
    TOKEN,

    /**
     * Revocation based on token claims.
     */
    CLAIM,

    /**
     * Revocation of all tokens.
     */
    GLOBAL
}