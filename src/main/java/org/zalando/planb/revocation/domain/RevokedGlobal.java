package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * Holds information about a global revocation.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevokedGlobal.class)
public abstract class RevokedGlobal implements RevokedInfo, RevokedData {

    /**
     * Returns a UNIX Timestamp (UTC) indicating that tokens issued before it are revoked.
     * <p>
     * <p>When not set, defaults to the current UNIX timestamp.</p>
     *
     * @return the aforementioned UNIX Timestamp (UTC)
     */
    @Value.Default
    public Integer issuedBefore() {
        return UnixTimestamp.now();
    }
}