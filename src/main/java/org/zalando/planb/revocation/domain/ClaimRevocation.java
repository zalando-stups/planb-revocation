package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Data
@NoArgsConstructor
public class ClaimRevocation implements RevocationData {
    private String name;

    @JsonProperty("value_hash")
    private String valueHash;

    @JsonProperty("issued_before")
    private Long issuedBefore;
}
