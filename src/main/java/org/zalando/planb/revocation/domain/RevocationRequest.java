package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Holds a new revocation request.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevocationRequest.class)
public interface RevocationRequest {

    /**
     * Returns the type of this revocation request.
     *
     * @return the type of this revocation request
     */
    RevocationType type();

    /**
     * Returns the details of this revocation request.
     *
     * @return the details of this revocation request.
     */
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