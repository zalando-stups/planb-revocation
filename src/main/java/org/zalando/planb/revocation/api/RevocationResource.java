package org.zalando.planb.revocation.api;

import org.springframework.http.HttpEntity;

import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationInfo;

/**
 * Resource to get and post revocations.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public interface RevocationResource {

    /**
     * Returns all the revocations since the specified timestamp.
     *
     * <p>Also returns meta information which may be of importance to the client, like a refresh notification.</p>
     *
     * @param   from  instant from when tokens were revoked, in UTC UNIX timestamp
     *
     * @return  all the revocations since the specified timestamp
     */
    RevocationInfo get(int from);

    /**
     * Posts the specified revocation to be stored.
     *
     * <p>Revokes tokens associated with the specified revocation type.</p>
     *
     * @param   revocation  the revocation associated with the tokens to revoke
     *
     * @return  HTTP Status {@code CREATED}, if the revocation was successfully stored
     */
    HttpEntity<String> post(Revocation revocation);
}
