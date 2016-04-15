package org.zalando.planb.revocation.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.yaml.snakeyaml.Yaml;
import org.zalando.planb.revocation.api.exception.YamlParsingException;
import org.zalando.planb.revocation.service.SwaggerService;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
public class SwaggerFromYamlFileService implements SwaggerService {

    private final ApplicationContext context;

    private final String yamlResource;

    private String swaggerJson;

    public SwaggerFromYamlFileService(ApplicationContext context, final String yamlResource) {
        this.context = context;
        this.yamlResource = yamlResource;
    }

    @Override
    public String swaggerDefinition() {
        return swaggerJson != null ? swaggerJson : convertToJson();
    }

    private String convertToJson() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Yaml yaml = new Yaml();

        try(InputStream in = context.getResource(yamlResource).getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in))) {

            String line;

            while ((line = r.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (final IOException e) {
            throw new YamlParsingException();
        }

        return new JSONObject(yaml.loadAs(stringBuilder.toString(), Map.class)).toString();
    }
}
