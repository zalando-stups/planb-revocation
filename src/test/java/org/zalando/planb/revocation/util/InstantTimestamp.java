package org.zalando.planb.revocation.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by rreis on 10/03/16.
 */
public enum InstantTimestamp {

    // only use in tests!
    NOW(UnixTimestamp.now()),
    FIVE_MINUTES_AGO(UnixTimestamp.now() - 300),
    FIVE_MINUTES_AFTER(UnixTimestamp.now() + 300),
    ONE_HOUR_AGO(UnixTimestamp.now() - 3600);

    private final int unixTimestamp;

    InstantTimestamp(int unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }

    public int seconds() { return unixTimestamp; };
}
