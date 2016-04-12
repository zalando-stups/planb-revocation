package org.zalando.planb.revocation.util.domain;

import org.zalando.planb.revocation.domain.ImmutableRevokedClaimsData;
import org.zalando.planb.revocation.domain.ImmutableRevokedClaimsInfo;
import org.zalando.planb.revocation.domain.ImmutableRevokedGlobal;
import org.zalando.planb.revocation.domain.ImmutableRevokedTokenData;
import org.zalando.planb.revocation.domain.ImmutableRevokedTokenInfo;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.RevokedData;
import org.zalando.planb.revocation.domain.RevokedInfo;
import org.zalando.planb.revocation.util.InstantTimestamp;

import java.util.Arrays;

/**
 * Utility classes for generating domain objects in tests.
 */
public class DomainUtils {

    /*
     * Revoked Data
     */
    private final static String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiIxIiwibmFtZSI6InJyZWlzIiwiYWRtaW4iOnRydWV9." +
            "UlZhyvrY9e7tRU88l8sfRb37oWGiL2t4insnO9Nsn1c";

    private final static Integer ISSUED_BEFORE = InstantTimestamp.FIVE_MINUTES_AGO.seconds();

    private final static String CLAIMS[][] = {
            {"uid", "011011100"},
            {"sub", "test0"}
    };

    /*
     * Revoked Info
     */
    private final static String TOKEN_HASH = "cgWc1EpFBvg31Qxr0lpviEkhAwp64Z-9MhaIIv94RiM=";

    private final static String HASH_ALGORITHM = "SHA-256";

    private final static String CLAIM_NAMES[];

    static {
        CLAIM_NAMES = new String[CLAIMS.length];

        for (int i = 0; i < CLAIMS.length; i++) {
            CLAIM_NAMES[i] = CLAIMS[i][0];
        }
    }

    private final static String JSON_CLAIM_NAMES;

    // Workaround to add double quotes around claim names (for JSON comparison)
    static {
        StringBuilder jsonNamesBuilder = new StringBuilder();
        for (int i = 0; i < CLAIMS.length; i++) {
            jsonNamesBuilder.append("\"" + CLAIMS[i][0] + "\",");
        }
        JSON_CLAIM_NAMES = "[" + jsonNamesBuilder.substring(0, jsonNamesBuilder.length() - 1) + "]";
    }

    private final static String VALUE_HASH = "CDUg1ANEiZnh5rGFNqUiU4d5TrbtwLNkOgtpjSu3B0s=";

    private final static Character SEPARATOR = '|';

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

    public final static RevokedInfo REVOKED_INFO[] = new RevokedInfo[]{
            ImmutableRevokedTokenInfo.builder()
                    .tokenHash(TOKEN_HASH)
                    .hashAlgorithm(HASH_ALGORITHM)
                    .issuedBefore(ISSUED_BEFORE)
                    .build(),
            ImmutableRevokedClaimsInfo.builder()
                    .names(Arrays.asList(CLAIM_NAMES))
                    .valueHash(VALUE_HASH)
                    .hashAlgorithm(HASH_ALGORITHM)
                    .separator(SEPARATOR)
                    .issuedBefore(ISSUED_BEFORE)
                    .build(),
            ImmutableRevokedGlobal.builder()
                    .issuedBefore(ISSUED_BEFORE)
                    .build()
    };

    public final static String SERIALIZED_REVOKED_INFO[] = new String[]{
            "{" +
                    "\"token_hash\":\"" + TOKEN_HASH + "\"," +
                    "\"hash_algorithm\":\"" + HASH_ALGORITHM + "\"," +
                    "\"issued_before\":" + ISSUED_BEFORE +
                    "}",
            "{" +
                    "\"names\":" + JSON_CLAIM_NAMES + "," +
                    "\"value_hash\":\"" + VALUE_HASH + "\"," +
                    "\"hash_algorithm\":\"" + HASH_ALGORITHM + "\"," +
                    "\"separator\":\"" + SEPARATOR + "\"," +
                    "\"issued_before\":" + ISSUED_BEFORE + "" +
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

    public static RevokedInfo revokedInfo(RevocationType type) {
        return REVOKED_INFO[type.ordinal()];
    }

    public static String revokedInfoJson(RevocationType type) {
        return SERIALIZED_REVOKED_INFO[type.ordinal()];
    }
}
