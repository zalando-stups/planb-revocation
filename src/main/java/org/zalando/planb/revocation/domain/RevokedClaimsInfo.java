package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <ul>
 *     <li>{@code names}: List with claim names;</li>
 *     <li>{@code valueHash}: The revoked claim values, concatenated using a separator character (default is '|').
 *     The string hashed using {@code hash_algorithm}, in URL Base64 encoding;
 *     </li>
 *     <li>{@code separator}: The character used to concatenate claim values;</li>
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
public class RevokedClaimsInfo implements RevokedInfo {
    private List<String> names;

    @JsonProperty("value_hash")
    private String valueHash;

    @JsonProperty("hash_algorithm")
    private String hashAlgorithm;

    private Character separator;

    @JsonProperty("issued_before")
    private Integer issuedBefore;
}
