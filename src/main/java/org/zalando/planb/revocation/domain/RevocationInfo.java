package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * TODO: small javadoc
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevocationInfo.class)
public interface RevocationInfo {

    RevocationType type();

    Integer revokedAt();

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
