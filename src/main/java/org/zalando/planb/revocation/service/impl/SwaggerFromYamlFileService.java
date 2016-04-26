package org.zalando.planb.revocation.service.impl;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.zalando.planb.revocation.api.exception.YamlParsingException;
import org.zalando.planb.revocation.service.SwaggerService;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
public class SwaggerFromYamlFileService implements SwaggerService {

    private final String yamlResource;

    private String swaggerJson;

    public SwaggerFromYamlFileService(final String yamlResource) {
        Assert.hasText(yamlResource, "'yamlResource' should never be null or empty");
        this.yamlResource = yamlResource;
    }

    @Override
    public String swaggerDefinition() {
        return swaggerJson != null ? swaggerJson : convertToJson();
    }

    protected String convertToJson() {
        try {
            swaggerJson = new JSONObject(new Yaml().loadAs(getClass().getResourceAsStream(yamlResource), Map.class))
                    .toString();
            return swaggerJson;
        } catch (YAMLException e) {
            throw new YamlParsingException();
        }
    }
}
