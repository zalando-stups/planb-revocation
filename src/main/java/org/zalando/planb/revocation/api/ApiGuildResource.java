package org.zalando.planb.revocation.api;

import org.springframework.http.HttpEntity;

public interface ApiGuildResource {

    HttpEntity<String> swaggerInfo();
}
