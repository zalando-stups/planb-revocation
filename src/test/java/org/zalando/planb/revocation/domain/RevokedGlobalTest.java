package org.zalando.planb.revocation.domain;

import org.junit.Test;
import org.zalando.planb.revocation.util.InstantTimestamp;
import org.zalando.planb.revocation.util.UnixTimestamp;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for global revocations.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class RevokedGlobalTest extends AbstractDomainTest {

    private final static Integer ISSUED_BEFORE = InstantTimestamp.FIVE_MINUTES_AGO.seconds();

    private final static String SERIALIZED_GLOBAL = "{" +
            "\"issued_before\":" + ISSUED_BEFORE +
            "}";

    private final static String SERIALIZED_GLOBAL_DEFAULTS = "{" +
            "}";

    /**
     * Tests that when instantiating a {@link RevokedGlobal} all values are set, including the defaults.
     */
    @Test
    public void testDefaultsAreSet() {

        RevokedGlobal global = ImmutableRevokedGlobal.builder().build();

        assertThat(global.issuedBefore())
                .isNotNull()
                .isLessThanOrEqualTo(UnixTimestamp.now());
    }

    /**
     * Tests JSON serialization of a {@link RevokedGlobal} object.
     */
    @Test
    public void testJsonSerialization() throws IOException {

        RevokedGlobal global = ImmutableRevokedGlobal.builder().issuedBefore(ISSUED_BEFORE).build();
        String serialized = objectMapper.writeValueAsString(global);

        assertThat(serialized).isEqualTo(SERIALIZED_GLOBAL);
    }

    /**
     * Tests JSON deserialization of a {@link RevokedGlobal} object.
     */
    @Test
    public void testJsonDeserialization() throws IOException {

        RevokedGlobal global = objectMapper.readValue(SERIALIZED_GLOBAL, RevokedGlobal.class);

        assertThat(global.issuedBefore()).isEqualTo(ISSUED_BEFORE);
    }

    /**
     * Tests JSON deserialization of a {@link RevokedGlobal} object when the serialized string does not have values that
     * should be set by default.
     */
    @Test
    public void testJsonDeserializationSetsDefaults() throws IOException {

        RevokedGlobal global = objectMapper.readValue(SERIALIZED_GLOBAL_DEFAULTS, RevokedGlobal.class);

        assertThat(global.issuedBefore())
                .isNotNull()
                .isLessThanOrEqualTo(UnixTimestamp.now());
    }
}
