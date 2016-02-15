package org.zalando.planb.revocation.persistence;

import java.io.IOException;

/**
 * Created by jmussler on 15.02.16.
 */
public interface RevocationDataMapper {
    RevocationData get(String data) throws IOException;
}
