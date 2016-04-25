package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Holds information about a revoked token.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Team Greendale</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevokedTokenInfo.class)
public interface RevokedTokenInfo extends RevokedInfo {

    /**
     * Returns the revoked token, hashed using {@link RevokedTokenInfo#hashAlgorithm()}, in URL Base64 encoding.
     *
     * @return the revoked token, hashed
     */
    String tokenHash();

    /**
     * Returns the algorithm used for hashing the Token.
     *
     * @return the algorithm used for hashing the Token
     */
    String hashAlgorithm();

    /**
     * Returns a UNIX Timestamp (UTC), indicating that the token is revoked if issued before it.
     *
     * @return the UNIX Timestamp (UTC)
     */
    Integer issuedBefore();
}
