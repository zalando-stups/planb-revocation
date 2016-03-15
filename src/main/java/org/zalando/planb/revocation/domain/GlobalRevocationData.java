package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <ul>
 *     <li>{@code issuedBefore}: a UNIX Timestamp (UTC) indicating that tokens issued before it are revoked.</li>
 * </ul>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Team Greendale</a>
 */
@Data
@NoArgsConstructor
public class GlobalRevocationData implements RevocationData {

    @JsonProperty("issued_before")
    private Integer issuedBefore;
}
