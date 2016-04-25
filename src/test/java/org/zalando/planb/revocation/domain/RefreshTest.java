package org.zalando.planb.revocation.domain;

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
 * Unit tests for refresh notifications.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PlanBRevocationConfig.class)
public class RefreshTest {

    @Autowired
    private ObjectMapper objectMapper;

    private static final Integer REFRESH_FROM = InstantTimestamp.FIVE_MINUTES_AGO.seconds();

    private static final Integer REFRESH_TIMESTAMP = InstantTimestamp.NOW.seconds();

    private static final String SERIALIZED_REFRESH = "{\"refresh_from\":" + REFRESH_FROM + "," +
            "\"refresh_timestamp\":" + REFRESH_TIMESTAMP + "}";

    private static final String SERIALIZED_REFRESH_WITHOUT_TIMESTAMP = "{\"refresh_from\":" + REFRESH_FROM + "}";

    /**
     * Tests that when instantiating a {@link Refresh} all values are set, including a default timestamp.
     */
    @Test
    public void testDefaultTimestamp() {
        Refresh notification = ImmutableRefresh.builder().refreshFrom(InstantTimestamp
                .FIVE_MINUTES_AGO.seconds()).build();

        assertThat(notification.refreshTimestamp()).isNotNull();
        assertThat(notification.refreshTimestamp()).isLessThanOrEqualTo(UnixTimestamp.now());
    }

    /**
     * Tests JSON serialization of a {@link Refresh} object.
     */
    @Test
    public void testJsonSerialization() throws IOException {

        Refresh notification = ImmutableRefresh.builder().refreshFrom(REFRESH_FROM).refreshTimestamp(REFRESH_TIMESTAMP)
                .build();

        String serialized = objectMapper.writeValueAsString(notification);
        assertThat(serialized).isEqualTo(SERIALIZED_REFRESH);
    }

    /**
     * Tests JSON deserialization of a {@link Refresh} object.
     */
    @Test
    public void testJsonDeserialization() throws IOException {

        Refresh notification = objectMapper.readValue(SERIALIZED_REFRESH, Refresh.class);

        assertThat(notification.refreshFrom()).isEqualTo(REFRESH_FROM);
        assertThat(notification.refreshTimestamp()).isEqualTo(REFRESH_TIMESTAMP);
    }

    /**
     * Tests JSON deserialization of a {@link Refresh} object when the serialized string does not have a timestamp
     * value.
     */
    @Test
    public void testJsonDeserializationWithoutTimestamp() throws IOException {

        Refresh notification = objectMapper.readValue(SERIALIZED_REFRESH_WITHOUT_TIMESTAMP, Refresh.class);

        assertThat(notification.refreshFrom()).isEqualTo(REFRESH_FROM);
        assertThat(notification.refreshTimestamp()).isNotNull();
        assertThat(notification.refreshTimestamp()).isLessThanOrEqualTo(UnixTimestamp.now());
    }
}
