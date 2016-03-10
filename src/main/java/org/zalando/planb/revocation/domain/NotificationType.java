package org.zalando.planb.revocation.domain;

import org.zalando.planb.revocation.api.RevocationResource;

/**
 * Notification types available as meta information.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Team Greendale</a>
 */
public enum NotificationType {
    /**
     * Used to inform clients of the maximum time since when it is possible to get revocations.
     */
    MAX_TIME_DELTA(false),
    /**
     * Informs clients that a refresh of revocations needs to be done, starting from a specified instant.
     */
    REFRESH_FROM(true),
    /**
     * Informs clients when {@link NotificationType#REFRESH_FROM} was set.
     */
    REFRESH_TIMESTAMP(false);

    private final Boolean isSettable;

    NotificationType(boolean isSettable) {
        this.isSettable = isSettable;
    }

    /**
     * Returns if the Notification Type can be set in a post to a {@link RevocationResource}.
     *
     * @return {@code true} if can be set, {@code false} otherwise
     */
    public boolean isSettable() {
        return isSettable;
    }
}
