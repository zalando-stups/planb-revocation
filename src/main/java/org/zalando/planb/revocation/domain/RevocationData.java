package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Data
@NoArgsConstructor
public class RevocationData {

    private RevocationType type;

    @JsonProperty("revoked_at")
    private Integer revokedAt = UnixTimestamp.now();

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
