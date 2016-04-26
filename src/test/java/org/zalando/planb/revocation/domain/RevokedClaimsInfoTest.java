package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Test;
import org.zalando.planb.revocation.util.InstantTimestamp;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RevokedClaimsInfo.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class RevokedClaimsInfoTest extends AbstractDomainTest {

    private final static String[] NAMES = {"uid", "azp"};

    private final static String JSON_NAMES;

    // Workaround to add double quotes around claim names (for JSON comparison)
    static {
        StringBuilder jsonNamesBuilder = new StringBuilder();
        for (String NAME : NAMES) {
            jsonNamesBuilder.append("\"").append(NAME).append("\",");
        }
        JSON_NAMES = "[" + jsonNamesBuilder.substring(0, jsonNamesBuilder.length() - 1) + "]";
    }

    private final static String VALUE_HASH = "CDUg1ANEiZnh5rGFNqUiU4d5TrbtwLNkOgtpjSu3B0s=";

    private final static String HASH_ALGORITHM = "SHA-256";

    private final static Character SEPARATOR = '|';

    private final static Integer ISSUED_BEFORE = InstantTimestamp.FIVE_MINUTES_AGO.seconds();

    private final static String SERIALIZED = "{" +
            "\"names\":" + JSON_NAMES + "," +
            "\"value_hash\":\"" + VALUE_HASH + "\"," +
            "\"hash_algorithm\":\"" + HASH_ALGORITHM + "\"," +
            "\"separator\":\"" + SEPARATOR + "\"," +
            "\"issued_before\":" + ISSUED_BEFORE + "" +
            "}";

    private final static String SERIALIZED_INCOMPLETE = "{" +
            "\"names\":" + JSON_NAMES + "," +
            "\"value_hash\":\"" + VALUE_HASH + "\"," +
            "\"separator\":\"" + SEPARATOR + "\"," +
            "\"issued_before\":" + ISSUED_BEFORE + "" +
            "}";

    /**
     * Tests that an exception is thrown when not setting values.
     */
    @Test(expected = IllegalStateException.class)
    public void testExceptionWhenNullValues() {
        ImmutableRevokedClaimsInfo.builder().build();
    }

    /**
     * Tests JSON serialization of a {@link RevokedClaimsInfo} object.
     */
    @Test
    public void testJsonSerialization() throws IOException {
        RevokedClaimsInfo rci = ImmutableRevokedClaimsInfo.builder()
                .addNames(NAMES)
                .valueHash(VALUE_HASH)
                .hashAlgorithm(HASH_ALGORITHM)
                .separator(SEPARATOR)
                .issuedBefore(ISSUED_BEFORE)
                .build();

        String serialized = objectMapper.writeValueAsString(rci);
        assertThat(serialized).isEqualTo(SERIALIZED);
    }

    /**
     * Tests JSON deserialization of a {@link RevokedClaimsInfo} object.
     */
    @Test
    public void testJsonDeserialization() throws IOException {

        RevokedClaimsInfo rci = objectMapper.readValue(SERIALIZED, RevokedClaimsInfo.class);

        assertThat(rci.names().toArray()).isEqualTo(NAMES);
        assertThat(rci.valueHash()).isEqualTo(VALUE_HASH);
        assertThat(rci.hashAlgorithm()).isEqualTo(HASH_ALGORITHM);
        assertThat(rci.separator()).isEqualTo(SEPARATOR);
        assertThat(rci.issuedBefore()).isEqualTo(ISSUED_BEFORE);
    }

    /**
     * Tests that JSON deserialization of a {@link RevokedClaimsInfo} object fails when not all values are set.
     */
    @Test(expected = JsonMappingException.class)
    public void testJsonDeserializationSFails() throws IOException {

        objectMapper.readValue(SERIALIZED_INCOMPLETE, RevokedClaimsInfo.class);
    }
}
