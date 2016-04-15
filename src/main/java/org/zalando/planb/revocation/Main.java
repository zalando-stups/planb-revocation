package org.zalando.planb.revocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class Main {

    public static void main(final String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }
}
