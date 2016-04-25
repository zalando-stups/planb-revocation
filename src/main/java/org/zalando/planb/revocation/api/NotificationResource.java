package org.zalando.planb.revocation.api;

import org.springframework.http.HttpEntity;
import org.zalando.planb.revocation.domain.NotificationType;

import java.util.EnumMap;

/**
 * Resource to post notification information, like a revocation refresh.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public interface NotificationResource {

    /**
     * Posts the specified notification to be stored.
     *
     * @param type  the type of notification
     * @param value the value of the notification to be stored
     */
    void post(NotificationType type, Object value);
}
