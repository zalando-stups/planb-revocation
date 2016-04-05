package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * <ul>
 *     <li>{@code issuedBefore}: a UNIX Timestamp (UTC) indicating that tokens issued before it are revoked.</li>
 * </ul>
 *
 * <p>When posting Global revocations, if {@code issuedBefore} is not set, it will default to the current UNIX
 * timestamp (UTC).</p>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Team Greendale</a>
 */
@Data
@NoArgsConstructor
public class RevokedGlobal implements RevokedInfo, RevokedData {

    @JsonProperty("issued_before")
    private Integer issuedBefore = UnixTimestamp.now();
}
