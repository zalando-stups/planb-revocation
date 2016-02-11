package org.zalando.planb.revocation.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.zalando.planb.revocation.domain.RevocationInfo;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@RequestMapping(value = "/api/revocations", produces = MediaType.APPLICATION_JSON_VALUE)
public interface RevocationResource {

    @RequestMapping(method = RequestMethod.GET)
    HttpEntity<RevocationInfo> get();

    @RequestMapping(method = RequestMethod.POST)
    HttpEntity<String> post(RevocationInfo rev);
}
