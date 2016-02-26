package org.zalando.planb.revocation.api;

import org.springframework.http.HttpEntity;

import org.zalando.planb.revocation.domain.Revocation;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
public interface RevocationResource {

    String get(Long from) throws JsonProcessingException;

    HttpEntity<String> post(Revocation revocations);
}
