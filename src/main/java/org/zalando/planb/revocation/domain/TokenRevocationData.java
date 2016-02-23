package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Data
public class TokenRevocationData implements RevocationData {
    @JsonProperty("token_hash")
    private String tokenHash;

    @JsonProperty("hash_algorithm")
    private String hashAlgorithm;
}
