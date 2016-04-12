package org.zalando.planb.revocation;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zalando.planb.revocation.config.properties.ApiGuildProperties;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contains generic tests for endpoints.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("test")
public class EndpointsTest extends AbstractSpringTest {

    @Value("${local.management.port}")
    private int mgmtPort;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ApiGuildProperties apiGuildProperties;

    private MockMvc mvc;

    @Before
    public void setupMockMcv() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testSwaggerEndpoint() throws Exception {
        mvc.perform(get("/swagger.json").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swagger", is("2.0")));
    }

    @Test
    public void testSchemaDiscoveryEndpoint() throws Exception {
        mvc.perform(get("/.well-known/schema-discovery").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schema_url", is("/swagger.json")));
    }

    @Test
    public void testHealthEndpoint() {
        ResponseEntity<String> response = getRestTemplate()
                .getForEntity(URI.create("http://localhost:" + mgmtPort + "/health"), String.class);
        JSONObject jsonBody = new JSONObject(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jsonBody.getString("status")).isEqualTo("UP");
    }

    @Test
    public void testMetricsEndpoint() {
        ResponseEntity<String> response = getRestTemplate()
                .getForEntity(URI.create("http://localhost:" + mgmtPort + "/metrics"), String.class);
        JSONObject jsonBody = new JSONObject(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jsonBody.getInt("mem")).isNotNull();
    }
}
