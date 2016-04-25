package org.zalando.planb.revocation.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.zalando.planb.revocation.api.exception.YamlParsingException;

public class SwaggerFromYamlFileServiceTest {

    @Test
    public void testService() {
        SwaggerFromYamlFileService service = new SwaggerFromYamlFileService("/api/swagger.yml");
        service = Mockito.spy(service);
        service.swaggerDefinition();
        String json = service.swaggerDefinition();
        Mockito.verify(service, Mockito.times(1)).convertToJson();
        System.out.println(json);
    }

    @Test(expected = YamlParsingException.class)
    public void testServiceFails() {
        SwaggerFromYamlFileService service = new SwaggerFromYamlFileService("/swagger.yml");
        service.swaggerDefinition();
    }

}
