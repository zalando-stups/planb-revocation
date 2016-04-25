package org.zalando.planb.revocation.util;

import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class UnixTimeStampTest {

    @Test
    public void testUnixTimestamp() {
        long unixTimestamp = UnixTimestamp.now();

        long millis = System.currentTimeMillis();
        long ut1 = UnixTimestamp.millisToSeconds(millis);
        long ut2 = UnixTimestamp.of(new Date(millis));

        Assertions.assertThat(ut1).isEqualTo(ut2);
        Assertions.assertThat(unixTimestamp).isLessThanOrEqualTo(ut1);
    }

}
