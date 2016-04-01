package org.zalando.planb.revocation.util;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Date;

public abstract class UnixTimestamp {

    private UnixTimestamp() {
    }

    public static int now() {
        return internalToSeconds(System.currentTimeMillis());
    }

    public static int of(Date date) {
        return internalToSeconds(date.getTime());
    }

    protected static int internalToSeconds(long millis) {
        return (int) MILLISECONDS.toSeconds(millis);
    }
}
