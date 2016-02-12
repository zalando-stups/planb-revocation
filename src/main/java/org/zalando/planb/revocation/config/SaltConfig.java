package org.zalando.planb.revocation.config;

import lombok.Data;

/**
 * Created by jmussler on 12.02.16.
 */
@Data
public class SaltConfig {
    String salt;
    long useFrom;
}
