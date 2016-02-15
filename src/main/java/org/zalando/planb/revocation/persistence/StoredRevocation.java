package org.zalando.planb.revocation.persistence;

import lombok.Data;
import org.zalando.planb.revocation.domain.RevocationType;

/**
 * Created by jmussler on 11.02.16.
 */
@Data
public class StoredRevocation {
    RevocationType type;
    RevocationData data;
    Long revokedAt;
    String revokedBy;

    public StoredRevocation(RevocationData data, RevocationType type, String revokedBy) {
        this.data = data;
        this.type = type;
        this.revokedBy = revokedBy;
        this.revokedAt = System.currentTimeMillis();
    }
}
