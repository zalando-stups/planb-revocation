package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.immutables.value.Value;

/**
 * Holds a list of revocations.
 * <p>
 * <p>Additionally, contains meta-information to inform requesting parties of important notifications.</p>
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
@JsonSerialize
@JsonDeserialize(as = ImmutableRevocationList.class)
public interface RevocationList {

    /**
     * Returns a map with meta information related to the revocation service.
     * <p>
     * <p>When there is no meta information, defaults to an empty map.</p>
     *
     * @return the aforementioned map
     */
    ImmutableMap<NotificationType, Object> meta();

    /**
     * Returns a list with revocations.
     * <p>
     * <p>Defaults to an empty list, when there are no revocations.</p>
     *
     * @return
     */
    ImmutableList<RevocationInfo> revocations();
}