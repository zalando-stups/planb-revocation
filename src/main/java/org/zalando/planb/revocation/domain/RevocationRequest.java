package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class RevocationRequest {

    private RevocationType type;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes(
        {
            @JsonSubTypes.Type(value = RevokedTokenData.class, name = "TOKEN"),
            @JsonSubTypes.Type(value = RevokedClaimsData.class, name = "CLAIM"),
            @JsonSubTypes.Type(value = RevokedGlobal.class, name = "GLOBAL"),
        }
    )
    private RevokedData data;
}
