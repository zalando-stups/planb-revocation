package org.zalando.planb.revocation.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.AddressTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.planb.revocation.config.properties.CassandraProperties;

import java.util.Optional;

@Configuration
@EnableConfigurationProperties(CassandraProperties.class)
public class CassandraConfig {


    @Autowired
    private CassandraProperties cassandraProperties;

    @Autowired
    private Optional<AddressTranslator> addressTranslator;

    @Bean
    @ConditionalOnProperty(prefix = "cassandra", name = "contact-points")
    public Session cassandraSession() {

        // Build Cluster
        final Cluster.Builder builder = Cluster.builder();
        builder.addContactPoints(cassandraProperties.getContactPoints().split(","));
        addressTranslator.ifPresent(builder::withAddressTranslator);
        builder.withClusterName(cassandraProperties.getClusterName());
        builder.withPort(cassandraProperties.getPort());

        // Only set credentials if they exist
        if (cassandraProperties.getUsername().isPresent() && cassandraProperties.getPassword().isPresent()) {
            builder.withCredentials(cassandraProperties.getUsername().get(), cassandraProperties.getPassword().get());
        }

        return builder.build().connect(cassandraProperties.getKeyspace());
    }


}
