package org.zalando.planb.revocation.api;

import org.springframework.http.HttpEntity;

import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationInfo;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
public interface RevocationResource {

    RevocationInfo get(Long from);

    HttpEntity<String> post(Revocation revocations);
}
