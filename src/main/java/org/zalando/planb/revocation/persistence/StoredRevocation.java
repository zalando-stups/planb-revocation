package org.zalando.planb.revocation.persistence;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.zalando.planb.revocation.domain.RevocationType;

/**
 * Created by jmussler on 11.02.16.
 */
@Data
@RequiredArgsConstructor
@Builder
public class StoredRevocation {
    private RevocationType type;

    private RevocationData data;

    private final Long revokedAt = System.currentTimeMillis();

    private String revokedBy;
}
