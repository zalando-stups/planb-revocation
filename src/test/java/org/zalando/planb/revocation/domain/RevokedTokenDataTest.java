package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.planb.revocation.config.PlanBRevocationConfig;
import org.zalando.planb.revocation.util.InstantTimestamp;
import org.zalando.planb.revocation.util.UnixTimestamp;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RevokedTokenData.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PlanBRevocationConfig.class)
public class RevokedTokenDataTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final static String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiIxIiwibmFtZSI6InJyZWlzIiwiYWRtaW4iOnRydWV9." +
            "UlZhyvrY9e7tRU88l8sfRb37oWGiL2t4insnO9Nsn1c";

    private final static Integer ISSUED_BEFORE = InstantTimestamp.FIVE_MINUTES_AGO.seconds();

    private final static String SERIALIZED = "{" +
            "\"token\":\"" + TOKEN + "\"," +
            "\"issued_before\":" + ISSUED_BEFORE +
            "}";

    private final static String SERIALIZED_DEFAULTS = "{" +
            "\"token\":\"" + TOKEN + "\"" +
            "}";

    private final static String SERIALIZED_INCOMPLETE = "{" +
            "\"issued_before\":" + ISSUED_BEFORE +
            "}";

    /**
     * Tests that an exception is thrown when not setting mandatory values.
     */
    @Test(expected = IllegalStateException.class)
    public void testExceptionWhenNullValues() {
        ImmutableRevokedTokenData.builder().build();
    }

    /**
     * Tests that when instantiating a {@link RevokedTokenData} all values are set, including the defaults.
     */
    @Test
    public void testDefaultsAreSet() {

        RevokedTokenData tokenData = ImmutableRevokedTokenData.builder().token(TOKEN).build();

        assertThat(tokenData.issuedBefore())
                .isNotNull()
                .isLessThanOrEqualTo(UnixTimestamp.now());
    }

    /**
     * Tests JSON serialization of a {@link RevokedTokenData} object.
     */
    @Test
    public void testJsonSerialization() throws IOException {
        RevokedTokenData tokenData = ImmutableRevokedTokenData.builder()
                .issuedBefore(ISSUED_BEFORE)
                .token(TOKEN)
                .build();

        String serialized = objectMapper.writeValueAsString(tokenData);
        assertThat(serialized).isEqualTo(SERIALIZED);
    }

    /**
     * Tests JSON deserialization of a {@link RevokedTokenData} object.
     */
    @Test
    public void testJsonDeserialization() throws IOException {

        RevokedTokenData tokenData = objectMapper.readValue(SERIALIZED, RevokedTokenData.class);

        assertThat(tokenData.issuedBefore()).isEqualTo(ISSUED_BEFORE);
        assertThat(tokenData.token()).isEqualTo(TOKEN);
    }

    /**
     * Tests that JSON deserialization of a {@link RevokedTokenData} object fails when mandatory values are not set.
     */
    @Test(expected = JsonMappingException.class)
    public void testJsonDeserializationFails() throws IOException {

        objectMapper.readValue(SERIALIZED_INCOMPLETE, RevokedTokenData.class);
    }

    /**
     * Tests JSON deserialization of a {@link RevokedTokenData} object when the serialized string does not have values
     * that are set by default.
     */
    @Test
    public void testJsonDeserializationSetsDefaults() throws IOException {

        RevokedTokenData tokenData = objectMapper.readValue(SERIALIZED_DEFAULTS, RevokedTokenData.class);

        assertThat(tokenData.issuedBefore())
                .isNotNull()
                .isLessThanOrEqualTo(UnixTimestamp.now());
    }
}
