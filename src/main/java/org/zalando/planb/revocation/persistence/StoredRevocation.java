package org.zalando.planb.revocation.persistence;

import org.zalando.planb.revocation.domain.RevocationType;

import lombok.Data;

/**
 * Created by jmussler on 11.02.16.
 */
@Data
public class StoredRevocation {
    RevocationType type;
    RevocationData data;
    Long revokedAt;
    String revokedBy;

    public StoredRevocation(final RevocationData data, final RevocationType type, final String revokedBy) {
        this.data = data;
        this.type = type;
        this.revokedBy = revokedBy;
        this.revokedAt = System.currentTimeMillis();
    }
}
