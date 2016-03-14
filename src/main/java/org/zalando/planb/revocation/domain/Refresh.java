package org.zalando.planb.revocation.domain;

import com.google.auto.value.AutoValue;
import org.zalando.planb.revocation.util.UnixTimestamp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Represents a notification to refresh all revocations since a point in time.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@AutoValue
public abstract class Refresh {

    Refresh() {}    // Avoid subclassing

    /**
     * Returns a builder for this class.
     * <p>
     * <p>By default {@link Refresh#refreshFrom()} is set to the present instant.</p>
     *
     * @return a builder for this class.
     */
    public static Builder builder() {
        return new AutoValue_Refresh.Builder().refreshTimestamp(UnixTimestamp.now());
    }

    /**
     * The instant from when to refresh notifications, in UTC UNIX timestamp.
     */
    public abstract Integer refreshFrom();

    /**
     * The instant that this refresh notification was created, in UTC UNIX Timestamp.
     */
    public abstract Integer refreshTimestamp();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder refreshFrom(Integer l);

        public abstract Builder refreshTimestamp(Integer l);

        public abstract Refresh build();
    }
}
