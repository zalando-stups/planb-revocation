package org.zalando.planb.revocation;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by jmussler on 15.02.16.
 */
public class LocalTimeFormatter {
    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL_DATEFORMAT = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df;
        }
    };

    public static SimpleDateFormat get() {
        return THREAD_LOCAL_DATEFORMAT.get();
    }
}
