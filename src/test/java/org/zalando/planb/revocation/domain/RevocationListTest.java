package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.planb.revocation.config.PlanBRevocationConfig;
import org.zalando.planb.revocation.util.InstantTimestamp;
import org.zalando.planb.revocation.util.domain.DomainUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RevocationList}.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PlanBRevocationConfig.class)
public class RevocationListTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final static Map<NotificationType, Object> META = ImmutableMap.of(
            NotificationType.REFRESH_FROM, InstantTimestamp.FIVE_MINUTES_AGO.seconds(),
            NotificationType.REFRESH_TIMESTAMP, InstantTimestamp.NOW.seconds());

    private final static List<RevocationInfo> REVOCATIONS = ImmutableList.of(
            DomainUtils.revocationInfo(RevocationType.TOKEN),
            DomainUtils.revocationInfo(RevocationType.CLAIM),
            DomainUtils.revocationInfo(RevocationType.GLOBAL));

    private final static String SERIALIZED = "{" +
            "\"meta\":" + "{" +
            "\"" + NotificationType.REFRESH_FROM + "\":" + META.get(NotificationType.REFRESH_FROM) + "," +
            "\"" + NotificationType.REFRESH_TIMESTAMP + "\":" + META.get(NotificationType.REFRESH_TIMESTAMP) +
            "}," +
            "\"revocations\":[" +
            DomainUtils.revocationInfoJson(RevocationType.TOKEN) + "," +
            DomainUtils.revocationInfoJson(RevocationType.CLAIM) + "," +
            DomainUtils.revocationInfoJson(RevocationType.GLOBAL) +
            "]}";

    private final static String SERIALIZED_DEFAULTS = "{" +
            "}";

    /**
     * Tests that when instantiating a {@link RevocationList} object all values are set, including the defaults.
     */
    @Test
    public void testDefaultsAreSet() {
        // All fields have defaults
        RevocationList testObject = ImmutableRevocationList.builder().build();

        assertThat(testObject.meta()).isNotNull().isEmpty();
        assertThat(testObject.revocations()).isNotNull().isEmpty();
    }

    /**
     * Tests JSON serialization of a {@link RevocationList} object.
     */
    @Test
    public void testJsonSerialization() throws IOException {
        RevocationList testObject = ImmutableRevocationList.builder()
                .meta(META)
                .revocations(REVOCATIONS)
                .build();

        String serialized = objectMapper.writeValueAsString(testObject);
        assertThat(serialized).isEqualTo(SERIALIZED);
    }

    /**
     * Tests JSON deserialization of a {@link RevocationList} object.
     */
    @Test
    public void testJsonDeserialization() throws IOException {

        RevocationList testObject = objectMapper.readValue(SERIALIZED, RevocationList.class);

        assertThat(testObject.meta()).isEqualTo(META);
        assertThat(testObject.revocations()).isEqualTo(REVOCATIONS);
    }

    /**
     * Tests JSON deserialization of a {@link RevocationList} object when the serialized string does not have values
     * that are set by default.
     */
    @Test
    public void testJsonDeserializationSetsDefaults() throws IOException {

        RevocationList testObject = objectMapper.readValue(SERIALIZED_DEFAULTS, RevocationList.class);

        assertThat(testObject.meta()).isNotNull().isEmpty();
        assertThat(testObject.revocations()).isNotNull().isEmpty();
    }
}