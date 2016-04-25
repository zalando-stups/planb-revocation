package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * Holds information about revoked claims.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevokedClaimsInfo.class)
public interface RevokedClaimsInfo extends RevokedInfo {

    /**
     * Returns a list with the claim names used in the revocation.
     *
     * @return a list with the claim names
     */
    List<String> names();

    /**
     * Returns the corresponding revoked claim values, concatenated using a separator character.
     * <p>
     * <p>The string is hashed using {@code hash_algorithm}, in URL Base64 encoding.</p>
     *
     * @return the hashed string, URL Base64 encoded
     */
    String valueHash();

    /**
     * Returns the algorithm used for hashing {@link RevokedClaimsInfo#valueHash()}.
     *
     * @return the hashing algorithm
     */
    String hashAlgorithm();

    /**
     * Returns the character used to concatenate values in {@link RevokedClaimsInfo#valueHash()}.</li>
     *
     * @return the concatenation character
     */
    Character separator();

    /**
     * Returns a UNIX Timestamp (UTC), indicating that the tokens related to this revocation are revoked if issued
     * before it.
     *
     * @return the UNIX Timestamp (UTC)
     */
    Integer issuedBefore();
}
