package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES;

public abstract class AbstractDomainTest {

    protected final ObjectMapper objectMapper;

    protected AbstractDomainTest() {
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        objectMapper.registerModule(new GuavaModule());
    }
}
