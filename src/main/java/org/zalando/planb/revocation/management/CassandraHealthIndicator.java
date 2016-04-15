package org.zalando.planb.revocation.management;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.zalando.planb.revocation.config.properties.CassandraProperties;

import javax.annotation.PostConstruct;

import static com.google.common.base.Preconditions.checkNotNull;

public class CassandraHealthIndicator extends AbstractHealthIndicator {

    private final Session session;
    private final CassandraProperties properties;
    private Statement healthCheck;

    public CassandraHealthIndicator(final Session session, final CassandraProperties properties) {
        this.properties = properties;
        this.session = checkNotNull(session, "Cassandra session must not be null");
    }

    @PostConstruct
    public void initializeHealthCheck() {
        this.healthCheck = this.session.prepare(properties.getHealthCheckQuery())
                .setConsistencyLevel(properties.getReadConsistencyLevel())
                .bind();
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        // if this throws an exception the AbstractHealthIndicator implementation will report this indicator as "down".
        session.execute(healthCheck);
        builder.up();
    }
}

