package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Test;
import org.zalando.planb.revocation.util.InstantTimestamp;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RevokedTokenInfo.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class RevokedTokenInfoTest extends AbstractDomainTest {

    private final static String TOKEN_HASH = "cgWc1EpFBvg31Qxr0lpviEkhAwp64Z-9MhaIIv94RiM=";

    private final static String HASH_ALGORITHM = "SHA-256";

    private final static Integer ISSUED_BEFORE = InstantTimestamp.FIVE_MINUTES_AGO.seconds();

    private final static String SERIALIZED = "{" +
            "\"token_hash\":\"" + TOKEN_HASH + "\"," +
            "\"hash_algorithm\":\"" + HASH_ALGORITHM + "\"," +
            "\"issued_before\":" + ISSUED_BEFORE +
            "}";

    private final static String SERIALIZED_INCOMPLETE = "{" +
            "\"token_hash\":\"" + TOKEN_HASH + "\"," +
            "\"hash_algorithm\":\"" + HASH_ALGORITHM + "\"" +
            "}";

    /**
     * Tests that an exception is thrown when not setting values.
     */
    @Test(expected = IllegalStateException.class)
    public void testExceptionWhenNullValues() {
        ImmutableRevokedTokenInfo.builder().build();
    }

    /**
     * Tests JSON serialization of a {@link RevokedTokenInfo} object.
     */
    @Test
    public void testJsonSerialization() throws IOException {
        RevokedTokenInfo rti = ImmutableRevokedTokenInfo.builder()
                .hashAlgorithm(HASH_ALGORITHM)
                .issuedBefore(ISSUED_BEFORE)
                .tokenHash(TOKEN_HASH)
                .build();

        String serialized = objectMapper.writeValueAsString(rti);
        assertThat(serialized).isEqualTo(SERIALIZED);
    }

    /**
     * Tests JSON deserialization of a {@link RevokedTokenInfo} object.
     */
    @Test
    public void testJsonDeserialization() throws IOException {

        RevokedTokenInfo rti = objectMapper.readValue(SERIALIZED, RevokedTokenInfo.class);

        assertThat(rti.hashAlgorithm()).isEqualTo(HASH_ALGORITHM);
        assertThat(rti.issuedBefore()).isEqualTo(ISSUED_BEFORE);
        assertThat(rti.tokenHash()).isEqualTo(TOKEN_HASH);
    }

    /**
     * Tests that JSON deserialization of a {@link RevokedTokenInfo} object fails when not all values are set.
     */
    @Test(expected = JsonMappingException.class)
    public void testJsonDeserializationFails() throws IOException {

        objectMapper.readValue(SERIALIZED_INCOMPLETE, RevokedTokenInfo.class);
    }
}
