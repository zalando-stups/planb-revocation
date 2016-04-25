package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Holds information about a single revocation.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevocationInfo.class)
public interface RevocationInfo {

    /**
     * Returns the type of the revocation.
     *
     * @return the type of the revocation
     */
    RevocationType type();

    /**
     * Returns the instant when the revocation was submitted.
     *
     * @return the instant when the revocation was submitted, in UTC Unix Timestamp format
     */
    Integer revokedAt();

    /**
     * Returns information about the revocation.
     *
     * @return information about the revocation
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes(
            {
                    @JsonSubTypes.Type(value = RevokedTokenInfo.class, name = "TOKEN"),
                    @JsonSubTypes.Type(value = RevokedClaimsInfo.class, name = "CLAIM"),
                    @JsonSubTypes.Type(value = RevokedGlobal.class, name = "GLOBAL"),
            }
    )
    RevokedInfo data();
}
