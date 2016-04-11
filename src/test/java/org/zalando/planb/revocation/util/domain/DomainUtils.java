package org.zalando.planb.revocation.util.domain;

import org.zalando.planb.revocation.domain.ImmutableRevokedTokenData;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.RevokedData;
import org.zalando.planb.revocation.domain.RevokedTokenData;

/**
 * Utility classes for generating domain objects int tests.
 */
public class DomainUtils {

    private final static String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiIxIiwibmFtZSI6InJyZWlzIiwiYWRtaW4iOnRydWV9." +
            "UlZhyvrY9e7tRU88l8sfRb37oWGiL2t4insnO9Nsn1c";

    public static RevokedData generateRevokedData(RevocationType type) {
        switch(type) {
            case TOKEN:
                RevokedTokenData a = ImmutableRevokedTokenData.builder().token(TOKEN).build();

                return ImmutableRevokedTokenData.builder().token(TOKEN).build();
        }

        return null;
    }
}
