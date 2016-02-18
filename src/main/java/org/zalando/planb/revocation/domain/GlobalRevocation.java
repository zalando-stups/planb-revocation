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
public class GlobalRevocation implements RevocationData {

    @JsonProperty("issued_before")
    private Long issuedBefore;
}
