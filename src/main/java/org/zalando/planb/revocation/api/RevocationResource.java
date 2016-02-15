package org.zalando.planb.revocation.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RequestParam;
import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationInfo;

import java.util.Collection;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@RequestMapping(value = "/api/revocations", produces = MediaType.APPLICATION_JSON_VALUE)
public interface RevocationResource {

    @RequestMapping(method = RequestMethod.GET)
    HttpEntity<RevocationInfo> get(@RequestParam(value="from") long from);

    @RequestMapping(method = RequestMethod.POST)
    HttpEntity<String> post(Revocation revocations);
}
