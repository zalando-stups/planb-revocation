package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.zalando.planb.revocation.util.UnixTimestamp;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the data of a new claims {@link RevocationRequest}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevokedClaimsData.class)
public abstract class RevokedClaimsData implements RevokedData {

    /**
     * Returns a map of claims and their correspondent values.</li>
     *
     * @return the aforementioned map
     */
    public abstract Map<String, String> claims();

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
