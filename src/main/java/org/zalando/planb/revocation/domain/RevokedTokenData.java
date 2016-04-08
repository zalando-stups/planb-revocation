package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * Holds the data of a new token {@link RevocationRequest}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevokedTokenData.class)
public abstract class RevokedTokenData implements RevokedData {

    /**
     * Returns the new OAuth2 token to revoke.
     *
     * @return the token to revoke
     */
    public abstract String token();

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
