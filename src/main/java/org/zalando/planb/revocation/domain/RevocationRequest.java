package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Holds a new revocation
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevocationRequest.class)
public interface RevocationRequest {

    RevocationType type();

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes(
            {
                    @JsonSubTypes.Type(value = RevokedTokenData.class, name = "TOKEN"),
                    @JsonSubTypes.Type(value = RevokedClaimsData.class, name = "CLAIM"),
                    @JsonSubTypes.Type(value = RevokedGlobal.class, name = "GLOBAL"),
            }
    )
    RevokedData data();
}
