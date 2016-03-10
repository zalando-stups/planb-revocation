package org.zalando.planb.revocation.api;

import org.springframework.http.HttpEntity;

import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationInfo;

/**
 * Resource to get and post revocations.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public interface RevocationResource {

    /**
     * Returns all the revocations since the specified timestamp.
     *
     * <p>Also returns meta information which may be of importance to the client, like a refresh notification.</p>
     *
     * @param from instant from when they were revoked, in UTC UNIX timestamp
     * @return all the revocations since the specified timestamp
     */
    RevocationInfo get(Long from);

    /**
     * Posts the specified revocation to be stored.
     *
     * @param revocation instant from when they were revoked, in UTC UNIX timestamp
     * @return HTTP Status {@code CREATED}, if the revocation was successfully stored
     */
    HttpEntity<String> post(Revocation revocation);
}
