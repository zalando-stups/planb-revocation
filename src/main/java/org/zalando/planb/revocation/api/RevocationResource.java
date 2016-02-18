package org.zalando.planb.revocation.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zalando.planb.revocation.domain.Revocation;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * TODO: small javadoc
 *
 * @author <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@RequestMapping(value = "/revocations", produces = MediaType.APPLICATION_JSON_VALUE)
public interface RevocationResource {

    @RequestMapping(method = RequestMethod.GET)
    HttpEntity<String> get(@RequestParam(value = "from", required = true) Long from) throws JsonProcessingException;

    @RequestMapping(method = RequestMethod.POST)
    HttpEntity<String> post(Revocation revocations);
}
