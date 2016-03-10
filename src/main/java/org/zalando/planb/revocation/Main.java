package org.zalando.planb.revocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = VelocityAutoConfiguration.class) // Needed because of Google AutoValue (WTF?)
public class Main {

    public static void main(final String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }
}
