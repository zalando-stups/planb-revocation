package org.zalando.planb.revocation.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by jmussler on 11.02.16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoredClaim extends RevocationData {

    private String claimName;

    private String claimValue;

    private int issuedBefore;
}
