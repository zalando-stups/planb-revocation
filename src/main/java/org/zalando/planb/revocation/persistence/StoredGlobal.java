package org.zalando.planb.revocation.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by jmussler on 11.02.16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoredGlobal extends RevocationData {
    int issued_before;
}
