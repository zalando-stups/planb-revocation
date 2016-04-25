package org.zalando.planb.revocation.util;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Date;

public abstract class UnixTimestamp {

    private UnixTimestamp() {
    }

    public static int now() {
        return millisToSeconds(System.currentTimeMillis());
    }

    public static int of(Date date) {
        return millisToSeconds(date.getTime());
    }

    protected static int millisToSeconds(long millis) {
        return (int) MILLISECONDS.toSeconds(millis);
    }
}
