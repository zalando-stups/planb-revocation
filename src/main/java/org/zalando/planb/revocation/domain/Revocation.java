package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Data
@NoArgsConstructor
public class Revocation {

    private RevocationType type;

    @JsonProperty("revoked_at")
    private Long revokedAt;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TokenRevocationData.class, name = "TOKEN"),
            @JsonSubTypes.Type(value = ClaimRevocation.class, name = "CLAIM"),
            @JsonSubTypes.Type(value = GlobalRevocation.class, name = "GLOBAL"),
    })
    private RevocationData data;
}
