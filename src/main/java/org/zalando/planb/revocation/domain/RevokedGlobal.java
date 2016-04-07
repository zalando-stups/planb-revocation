package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * <ul>
 * <li>{@code issuedBefore}: a UNIX Timestamp (UTC) indicating that tokens issued before it are revoked.</li>
 * </ul>
 * <p>
 * <p>When posting Global revocations, if {@code issuedBefore} is not set, it will default to the current UNIX
 * timestamp (UTC).</p>
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Team Greendale</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevokedGlobal.class)
public abstract class RevokedGlobal implements RevokedInfo, RevokedData {

    /**
     * Returns a UNIX Timestamp (UTC) indicating that tokens issued before it are revoked.
     * <p>
     * <p>When not set, defaults to to the current UNIX timestamp.</p>
     *
     * @return the aforementioned UNIX Timestamp (UTC)
     */
    @Value.Default
    public Integer issuedBefore() {
        return UnixTimestamp.now();
    }
}