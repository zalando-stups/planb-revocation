package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * <ul>
 *     <li>{@code tokenHash}: The revoked token hashed using hash_algorithm, in URL Base64 encoding;</li>
 *     <li>{@code hashAlgorithm}: The algorithm used for hashing the Token.</li>
 *     <li>{@code issuedBefore}: a UNIX Timestamp (UTC) indicating that tokens issued before it are revoked.</li>
 * </ul>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Team Greendale</a>
 */
@Data
public class RevokedTokenInfo implements RevokedInfo {
    @JsonProperty("token_hash")
    private String tokenHash;

    @JsonProperty("hash_algorithm")
    private String hashAlgorithm;

    @JsonProperty("issued_before")
    private Integer issuedBefore;
}
