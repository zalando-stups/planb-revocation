package org.zalando.planb.revocation.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by rreis on 10/03/16.
 */
public enum InstantTimestamp {

    NOW(LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC).toEpochMilli() / 1000),
    FIVE_MINUTES_AGO((LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC).toEpochMilli() / 1000) - 300),
    ONE_HOUR_AGO((LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC).toEpochMilli() / 1000) - 3600);

    private final long unixTimestamp;

    InstantTimestamp(Long unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }

    public long seconds() { return unixTimestamp; };
}
