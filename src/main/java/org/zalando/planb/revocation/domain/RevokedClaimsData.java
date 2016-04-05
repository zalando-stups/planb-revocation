package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zalando.planb.revocation.util.UnixTimestamp;

import java.util.Map;

/**
 * <ul>
 *     <li>{@code claims}: a dictionary of claims and their correspondent claim values;</li>
 *     <li>{@code issuedBefore}: tokens issued before this value - in UNIX Timestamp (UTC).
 * </ul>
 *
 * <p>When posting Claims, if {@code issuedBefore} is not set, it will default to the current UNIX timestamp (UTC).</p>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Data
@NoArgsConstructor
public class RevokedClaimsData implements RevokedData {

    private Map<String, String> claims;

    @JsonProperty("issued_before")
    private Integer issuedBefore = UnixTimestamp.now();
}
