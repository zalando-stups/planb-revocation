package org.zalando.planb.revocation.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.zalando.planb.revocation.api.ApiGuildResource;
import org.zalando.planb.revocation.service.SchemaDiscoveryService;
import org.zalando.planb.revocation.service.SwaggerService;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@RestController
public class ApiGuildResourceImpl implements ApiGuildResource {

    @Autowired
    private SwaggerService swaggerService;

    @Autowired
    private SchemaDiscoveryService schemaDiscoveryService;

    @Override
    @RequestMapping(method = RequestMethod.GET, value = "/swagger.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<String> swagger() {
        return new ResponseEntity<>(swaggerService.swaggerDefinition(), HttpStatus.OK);
    }

    @Override
    @RequestMapping(
        method = RequestMethod.GET, value = "/.well-known/schema-discovery",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public HttpEntity<String> schemaDiscovery() {
        return new ResponseEntity<>(schemaDiscoveryService.schemaDiscovery(), HttpStatus.OK);
    }
}
