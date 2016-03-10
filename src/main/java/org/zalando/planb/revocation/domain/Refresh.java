package org.zalando.planb.revocation.domain;

import com.google.auto.value.AutoValue;

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
        return new AutoValue_Refresh.Builder().refreshTimestamp(LocalDateTime.now(ZoneOffset.UTC)
                .toInstant(ZoneOffset.UTC).toEpochMilli() / 1000);
    }

    /**
     * The instant from when to refresh notifications, in UTC UNIX timestamp.
     */
    public abstract Long refreshFrom();

    /**
     * The instant that this refresh notification was created, in UTC UNIX Timestamp.
     */
    public abstract Long refreshTimestamp();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder refreshFrom(Long l);

        public abstract Builder refreshTimestamp(Long l);

        public abstract Refresh build();
    }
}
