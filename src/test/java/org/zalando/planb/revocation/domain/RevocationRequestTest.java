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
 * Unit tests for {@link RevocationRequest}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PlanBRevocationConfig.class)
public class RevocationRequestTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final static RevocationType TYPE = RevocationType.TOKEN;

    private final static String SERIALIZED = "{" +
            "\"type\":\"" + TYPE + "\"," +
            "\"data\":" + DomainUtils.revokedDataJson(RevocationType.TOKEN) +
            "}";

    private final static String SERIALIZED_INCOMPLETE = "{" +
            "\"data\":" + DomainUtils.revokedDataJson(RevocationType.TOKEN) +
            "}";

    /**
     * Tests that an exception is thrown when not setting values.
     */
    @Test(expected = IllegalStateException.class)
    public void testExceptionWhenNullValues() {
        ImmutableRevocationRequest.builder().build();
    }

    /**
     * Tests JSON serialization of a {@link RevocationRequest} object.
     */
    @Test
    public void testJsonSerialization() throws IOException {
        RevocationRequest testObject = ImmutableRevocationRequest.builder()
                .type(TYPE)
                .data(DomainUtils.revokedData(TYPE))
                .build();

        String serialized = objectMapper.writeValueAsString(testObject);
        assertThat(serialized).isEqualTo(SERIALIZED);
    }

    /**
     * Tests JSON deserialization of a {@link RevocationRequest} object.
     */
    @Test
    public void testJsonDeserialization() throws IOException {

        RevocationRequest testObject = objectMapper.readValue(SERIALIZED, RevocationRequest.class);

        assertThat(testObject.type()).isEqualTo(TYPE);
        assertThat(testObject.data()).isEqualTo(DomainUtils.revokedData(TYPE));
    }

    /**
     * Tests that JSON deserialization of a {@link RevocationRequest} object fails when not all values are set.
     */
    @Test(expected = JsonMappingException.class)
    public void testJsonDeserializationFails() throws IOException {

        objectMapper.readValue(SERIALIZED_INCOMPLETE, RevocationRequest.class);
    }
}
