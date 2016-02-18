package org.zalando.planb.revocation.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;

@Configuration
@EnableMetrics
public class MetricsConfigurer extends MetricsConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        if (environment.acceptsProfiles("it")) {
            registerReporter(ConsoleReporter.forRegistry(metricRegistry).build()).start(10, TimeUnit.SECONDS);
        }
    }

}
