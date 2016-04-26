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
import org.zalando.planb.revocation.util.domain.DomainUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RevocationInfo}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class RevocationInfoTest extends AbstractDomainTest {

    private final static RevocationType TYPE = RevocationType.TOKEN;

    private final static Integer REVOKED_AT = InstantTimestamp.FIVE_MINUTES_AGO.seconds();

    private final static String SERIALIZED = "{" +
            "\"type\":\"" + TYPE + "\"," +
            "\"revoked_at\":" + REVOKED_AT + "," +
            "\"data\":" + DomainUtils.revokedInfoJson(RevocationType.TOKEN) +
            "}";

    private final static String SERIALIZED_INCOMPLETE = "{" +
            "\"data\":" + DomainUtils.revokedInfoJson(RevocationType.TOKEN) +
            "}";

    /**
     * Tests that an exception is thrown when not setting values.
     */
    @Test(expected = IllegalStateException.class)
    public void testExceptionWhenNullValues() {
        ImmutableRevocationInfo.builder().build();
    }

    /**
     * Tests JSON serialization of a {@link RevocationInfo} object.
     */
    @Test
    public void testJsonSerialization() throws IOException {
        RevocationInfo testObject = ImmutableRevocationInfo.builder()
                .type(TYPE)
                .revokedAt(REVOKED_AT)
                .data(DomainUtils.revokedInfo(TYPE))
                .build();

        String serialized = objectMapper.writeValueAsString(testObject);
        assertThat(serialized).isEqualTo(SERIALIZED);
    }

    /**
     * Tests JSON deserialization of a {@link RevocationInfo} object.
     */
    @Test
    public void testJsonDeserialization() throws IOException {

        RevocationInfo testObject = objectMapper.readValue(SERIALIZED, RevocationInfo.class);

        assertThat(testObject.type()).isEqualTo(TYPE);
        assertThat(testObject.revokedAt()).isEqualTo(REVOKED_AT);
        assertThat(testObject.data()).isEqualTo(DomainUtils.revokedInfo(TYPE));
    }

    /**
     * Tests that JSON deserialization of a {@link RevocationInfo} object fails when not all values are set.
     */
    @Test(expected = JsonMappingException.class)
    public void testJsonDeserializationFails() throws IOException {

        objectMapper.readValue(SERIALIZED_INCOMPLETE, RevocationInfo.class);
    }
}
