package org.zalando.planb.revocation.config;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AuthenticationException;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.springframework.boot.Banner.Mode.OFF;

public class CassandraConfigIT {

    @Test
    public void testSuccessfulCassandraConnection() throws Exception {
        final ConfigurableApplicationContext applicationContext =
                new SpringApplicationBuilder(CassandraConfig.class)
                        .web(false)
                        .bannerMode(OFF)
                        .profiles("it")
                        .run();

        assertThat(applicationContext.getBean(Session.class)).isNotNull();

        applicationContext.close();
    }

    @Test
    public void testCassandraConnectionWithBadCredentials() throws Exception {
        try {
            new SpringApplicationBuilder(CassandraConfig.class)
                    .web(false)
                    .bannerMode(OFF)
                    .profiles("it")
                    .run("--cassandra.password=theWrongOne",
                            "--logging.level.org.springframework.boot.SpringApplication=OFF");
            failBecauseExceptionWasNotThrown(BeansException.class);
        } catch (BeansException e) {
            assertThat(e).hasRootCauseInstanceOf(AuthenticationException.class);
        }
    }

    @Test
    public void testCassandraConnectionWithMissingCredentials() throws Exception {
        try {
            new SpringApplicationBuilder(CassandraConfig.class)
                    .web(false)
                    .bannerMode(OFF)
                    .run("--cassandra.contact-points=127.0.0.1",
                            "--logging.level.org.springframework.boot.SpringApplication=OFF");
            failBecauseExceptionWasNotThrown(BeansException.class);
        } catch (BeansException e) {
            assertThat(e).hasRootCauseInstanceOf(AuthenticationException.class);
        }
    }

    @Test
    public void testCassandraConnectionWithMissingPassword() throws Exception {
        try {
            new SpringApplicationBuilder(CassandraConfig.class)
                    .web(false)
                    .bannerMode(OFF)
                    .run("--cassandra.contact-points=127.0.0.1",
                            "--cassandra.username=cassandra",
                            "--logging.level.org.springframework.boot.SpringApplication=OFF");
            failBecauseExceptionWasNotThrown(BeansException.class);
        } catch (BeansException e) {
            assertThat(e).hasRootCauseInstanceOf(AuthenticationException.class);
        }
    }

}
