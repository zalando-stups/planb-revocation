package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Data
@NoArgsConstructor
public class RevocationInfo {

    private RevocationType type;

    @JsonProperty("revoked_at")
    private Integer revokedAt;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes(
        {
            @JsonSubTypes.Type(value = RevokedTokenInfo.class, name = "TOKEN"),
            @JsonSubTypes.Type(value = RevokedClaimsInfo.class, name = "CLAIM"),
            @JsonSubTypes.Type(value = RevokedGlobal.class, name = "GLOBAL"),
        }
    )
    private RevokedInfo data;
}
