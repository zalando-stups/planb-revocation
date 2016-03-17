package org.zalando.planb.revocation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.zalando.planb.revocation.util.UnixTimestamp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Unit tests for domain objects.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class DomainTests {

    private static final int ONE_HOUR_BEFORE = 1457565076;

    private static final int ONE_MINUTE_BEFORE = 1457568706;

    /**
     * Tests that when instantiating a {@link Refresh} all values are set, including defaults.
     */
    @Test
    public void testDefaultRefresh() {

        Refresh r = Refresh.create(ONE_HOUR_BEFORE);
        assertThat(r.refreshFrom()).isEqualTo(ONE_HOUR_BEFORE);
        assertThat(r.refreshTimestamp()).isNotNull();
    }

    /**
     * Tests assigning a timestamp to a {@link Refresh} object.
     */
    @Test
    public void testRefreshWithTimestamp() {

        Refresh r = Refresh.create(ONE_HOUR_BEFORE, ONE_MINUTE_BEFORE);
        assertThat(r.refreshTimestamp()).isEqualTo(ONE_MINUTE_BEFORE);
    }

    /**
     * Tests JSON serialization of {@link Refresh} objects.
     */
    @Test
    public void testRefreshJsonSerialization() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = "{\"refresh_from\":1458232925,\"refresh_timestamp\":1458232985}";

        Refresh refresh = Refresh.create(1458232925, 1458232985);

        String serialized = mapper.writeValueAsString(refresh);

        assertThat(serialized).isEqualTo(expected);
    }

    @Test
    public void testRefreshJsonDeserialization() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"refresh_from\":1458232925,\"refresh_timestamp\":1458232985}";

        Refresh deserialized = mapper.readValue(json, Refresh.class);

        assertThat(deserialized.refreshFrom()).isEqualTo(1458232925);
        assertThat(deserialized.refreshTimestamp()).isEqualTo(1458232985);
    }
}
