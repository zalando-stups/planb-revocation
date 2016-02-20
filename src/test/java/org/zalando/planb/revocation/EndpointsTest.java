package org.zalando.planb.revocation;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.planb.revocation.api.ApiGuildResource;
import org.zalando.planb.revocation.api.RevocationResource;
import org.zalando.planb.revocation.api.impl.ApiGuildresourceImpl;
import org.zalando.planb.revocation.config.PlanBRevocationConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rreis on 2/20/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { MockServletContext.class, PlanBRevocationConfig.class })
@WebAppConfiguration
public class EndpointsTest {

    @Autowired
    RevocationResource resource;

    private MockMvc mvc;

    @Before
    public void before() {
        this.mvc = MockMvcBuilders.standaloneSetup(resource).build();
    }

    @Ignore
    @Test
    public void testSwaggerEndpoint() throws Exception {
        mvc.perform(get("/swagger.json").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
//        ResultActions startApp = null //mockMvc().perform(get("/.well-known/schema-discovery"));
//        startApp.andExpect(status().isOk());
//        startApp.andExpect(jsonPath("schema_url").exists());
//        startApp.andExpect(jsonPath("schema_type").exists());
    }

}
