package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <ul>
 *     <li>{@code name}: The claim of the tokens to revoke;</li>
 *     <li>{@code valueHash}: The revoked claim hashed using hash_algorithm, in URL Base64 encoding;</li>
 *     <li>{@code hashAlgorithm}: The algorithm used for hashing the Claim;</li>
 *     <li>{@code issuedBefore}: a UNIX Timestamp (UTC) indicating that tokens issued before it are revoked.</li>
 * </ul>
 *
 * <p>When posting a Claim Revocation, if {@code issuedBefore} is not set, it will default to the current UNIX
 * timestamp (UTC).</p>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Team Greendale</a>
 */
@Data
@NoArgsConstructor
public class ClaimRevocationData implements RevocationData {
    private String name;

    @JsonProperty("value_hash")
    private String valueHash;

    @JsonProperty("hash_algorithm")
    private String hashAlgorithm;

    @JsonProperty("issued_before")
    private Integer issuedBefore;
}
