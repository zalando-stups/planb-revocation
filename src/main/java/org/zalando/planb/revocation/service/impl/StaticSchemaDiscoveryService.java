package org.zalando.planb.revocation.service.impl;

import org.zalando.planb.revocation.service.SchemaDiscoveryService;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
public class StaticSchemaDiscoveryService implements SchemaDiscoveryService {

    private final String SCHEMA = "{\n    \"schema_url\": \"/swagger.json\",\n    \"schema_type\": \"swagger-2.0\"\n}";

    @Override
    public String schemaDiscovery() {
        return SCHEMA;
    }
}
