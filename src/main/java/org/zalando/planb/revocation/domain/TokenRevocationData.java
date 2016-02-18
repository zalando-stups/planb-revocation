package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: small javadoc
 *
 * @author <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Data
@NoArgsConstructor
public class TokenRevocationData implements RevocationData {
    @JsonProperty("token_hash")
    private String tokenHash;

    @JsonProperty("revoked_at")
    private Long revokedAt;
}
