package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * Holds data about a newly submitted revocation to be stored.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevocationData.class)
public abstract class RevocationData {

    /**
     * Returns the original revocation request to be stored.
     *
     * @return the original revocation request to be stored
     */
    public abstract RevocationRequest revocationRequest();

    /**
     * Returns the instant when the revocation was submitted.
     * <p>
     * <p>Defaults to the current UTC Unix Timestamp.</p>
     *
     * @return the instant when the revocation was submitted, in UTC Unix Timestamp format
     */
    @Value.Default
    public Integer revokedAt() {
        return UnixTimestamp.now();
    }
}
