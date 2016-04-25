package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * A notification to refresh all revocations since an instant in time.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRefresh.class)
public abstract class Refresh {

    /**
     * The instant from when to refresh notifications, in UTC UNIX timestamp.
     */
    public abstract Integer refreshFrom();

    /**
     * The instant that this refresh notification was created, in UTC UNIX Timestamp. Defaults to the current timestamp.
     */
    @Value.Default
    public Integer refreshTimestamp() {
        return UnixTimestamp.now();
    }
}
