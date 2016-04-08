package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * TODO: small javadoc
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevocationData.class)
public abstract class RevocationData {

    public abstract RevocationRequest revocationRequest();

    @Value.Default
    public Integer revokedAt() {
        return UnixTimestamp.now();
    }
}
