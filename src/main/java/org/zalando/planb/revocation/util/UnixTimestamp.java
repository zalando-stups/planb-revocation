package org.zalando.planb.revocation.util;

import java.util.Date;

public class UnixTimestamp {

    public static int now() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static int of(Date date) {
        return (int) (date.getTime() / 1000);
    }
}
