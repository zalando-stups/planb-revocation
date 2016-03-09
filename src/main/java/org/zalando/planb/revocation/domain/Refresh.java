package org.zalando.planb.revocation.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

/**
 * Represents a notification to refresh all revocations since a point in time.
 *
 * <ul>
 *   <li>{@code refreshFrom} - the instant from when to refresh notifications, in UTC UNIX timestamp;</li>
 *   <li>{@code refreshTimestamp} - the instant that this refresh notification was created, in UTC UNIX Timestamp.</li>
 * </ul>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Getter
public class Refresh {

    private final Long refreshFrom;

    private Long refreshTimestamp = Long.valueOf(System.currentTimeMillis() / 1000);

    @Builder
    private Refresh(Long refreshFrom, Long refreshTimestamp) {
        this.refreshFrom = refreshFrom;
        this.refreshTimestamp = refreshTimestamp;
    }
}
