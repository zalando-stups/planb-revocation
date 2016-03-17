package org.zalando.planb.revocation.domain;

import org.zalando.planb.revocation.util.UnixTimestamp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.auto.value.AutoValue;

/**
 * Represents a notification to refresh all revocations since a point in time.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@AutoValue
public abstract class Refresh {

    Refresh() { } // Avoid subclassing

    @JsonCreator
    public static Refresh create(@JsonProperty("refresh_from") final Integer refreshFrom,
            @JsonProperty("refresh_timestamp") final Integer refreshTimestamp) {
        return new AutoValue_Refresh(refreshFrom, refreshTimestamp != null ? refreshTimestamp : UnixTimestamp.now());
    }

    public static Refresh create(final Integer refreshFrom) {
        return create(refreshFrom, UnixTimestamp.now());
    }

    /**
     * The instant from when to refresh notifications, in UTC UNIX timestamp.
     */
    @JsonProperty("refresh_from")
    public abstract Integer refreshFrom();

    /**
     * The instant that this refresh notification was created, in UTC UNIX Timestamp.
     */
    @JsonProperty("refresh_timestamp")
    public abstract Integer refreshTimestamp();
}
