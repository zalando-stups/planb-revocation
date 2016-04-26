package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Test;
import org.zalando.planb.revocation.util.InstantTimestamp;
import org.zalando.planb.revocation.util.UnixTimestamp;
import org.zalando.planb.revocation.util.domain.DomainUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RevocationData}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class RevocationDataTest extends AbstractDomainTest {

    private final static RevocationType TYPE = RevocationType.TOKEN;

    private final static RevocationRequest REVOCATION_REQUEST = DomainUtils.revocationRequest(TYPE);

    private final static Integer REVOKED_AT = InstantTimestamp.FIVE_MINUTES_AGO.seconds();

    private final static String SERIALIZED = "{" +
            "\"revocation_request\":" + DomainUtils.revocationRequestJson(TYPE) + "," +
            "\"revoked_at\":" + REVOKED_AT +
            "}";

    private final static String SERIALIZED_INCOMPLETE = "{" +
            "\"revoked_at\":" + REVOKED_AT +
            "}";

    /**
     * Tests that an exception is thrown when not setting required values.
     */
    @Test(expected = IllegalStateException.class)
    public void testExceptionWhenNullValues() {
        ImmutableRevocationData.builder().build();
    }


    /**
     * Tests that when instantiating a {@link RevocationData} object all values are set, including the defaults.
     */
    @Test
    public void testDefaultsAreSet() {
        RevocationData testObject = ImmutableRevocationData.builder()
                .revocationRequest(REVOCATION_REQUEST)
                .build();

        assertThat(testObject.revokedAt())
                .isNotNull()
                .isLessThanOrEqualTo(UnixTimestamp.now());
    }

    /**
     * Tests JSON serialization of a {@link RevocationData} object.
     */
    @Test
    public void testJsonSerialization() throws IOException {
        RevocationData testObject = ImmutableRevocationData.builder()
                .revokedAt(REVOKED_AT)
                .revocationRequest(REVOCATION_REQUEST)
                .build();

        String serialized = objectMapper.writeValueAsString(testObject);
        assertThat(serialized).isEqualTo(SERIALIZED);
    }

    /**
     * Tests JSON deserialization of a {@link RevocationData} object.
     */
    @Test
    public void testJsonDeserialization() throws IOException {

        RevocationData testObject = objectMapper.readValue(SERIALIZED, RevocationData.class);

        assertThat(testObject.revokedAt()).isEqualTo(REVOKED_AT);
        assertThat(testObject.revocationRequest()).isEqualTo(DomainUtils.revocationRequest(TYPE));
    }

    /**
     * Tests that JSON deserialization of a {@link RevocationData} object fails when not all values are set.
     */
    @Test(expected = JsonMappingException.class)
    public void testJsonDeserializationFails() throws IOException {

        objectMapper.readValue(SERIALIZED_INCOMPLETE, RevocationData.class);
    }
}
