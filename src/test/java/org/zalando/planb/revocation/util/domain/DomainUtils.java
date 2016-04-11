package org.zalando.planb.revocation.util.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.zalando.planb.revocation.domain.ImmutableRevokedClaimsData;
import org.zalando.planb.revocation.domain.ImmutableRevokedGlobal;
import org.zalando.planb.revocation.domain.ImmutableRevokedTokenData;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.RevokedData;
import org.zalando.planb.revocation.util.InstantTimestamp;

/**
 * Utility classes for generating domain objects in tests.
 */
public class DomainUtils {

    private final static String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiIxIiwibmFtZSI6InJyZWlzIiwiYWRtaW4iOnRydWV9." +
            "UlZhyvrY9e7tRU88l8sfRb37oWGiL2t4insnO9Nsn1c";

    private final static Integer ISSUED_BEFORE = InstantTimestamp.FIVE_MINUTES_AGO.seconds();

    private final static String CLAIMS[][] = {
            {"uid", "011011100"},
            {"sub", "test0"}
    };

    public final static RevokedData REVOKED_DATA[] = new RevokedData[]{
            ImmutableRevokedTokenData.builder()
                    .issuedBefore(ISSUED_BEFORE)
                    .token(TOKEN)
                    .build(),
            ImmutableRevokedClaimsData.builder()
                    .issuedBefore(ISSUED_BEFORE)
                    .putClaims(CLAIMS[0][0], CLAIMS[0][1])
                    .putClaims(CLAIMS[1][0], CLAIMS[1][1])
                    .build(),
            ImmutableRevokedGlobal.builder().issuedBefore(ISSUED_BEFORE).build()
    };

    public final static String SERIALIZED_REVOKED_DATA[] = new String[]{
            "{" +
                    "\"token\":\"" + TOKEN + "\"," +
                    "\"issued_before\":" + ISSUED_BEFORE +
                    "}",
            "{" +
                    "\"claims\":{\"" + CLAIMS[0][0] + "\":\"" + CLAIMS[0][1] + "\"," +
                    "\"" + CLAIMS[1][0] + "\":\"" + CLAIMS[1][1] + "\"" +
                    "}," +
                    "\"issued_before\":" + ISSUED_BEFORE +
                    "}",
            "{" +
                    "\"issued_before\":" + ISSUED_BEFORE +
                    "}"
    };

    public static RevokedData revokedData(RevocationType type) {
        return REVOKED_DATA[type.ordinal()];
    }

    public static String revokedDataJson(RevocationType type) {
        return SERIALIZED_REVOKED_DATA[type.ordinal()];
    }
}
