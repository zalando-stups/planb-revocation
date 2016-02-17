package org.zalando.planb.revocation.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by jmussler on 11.02.16.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoredToken extends RevocationData {
    String tokenHash;
}
