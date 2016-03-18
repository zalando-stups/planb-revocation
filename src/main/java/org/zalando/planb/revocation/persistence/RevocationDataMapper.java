package org.zalando.planb.revocation.persistence;

import org.zalando.planb.revocation.domain.RevokedData;

import java.io.IOException;

/**
 * Created by jmussler on 15.02.16.
 */
public interface RevocationDataMapper {
    RevokedData get(String data) throws IOException;
}
