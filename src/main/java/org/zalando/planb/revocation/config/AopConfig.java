package org.zalando.planb.revocation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.zalando.planb.revocation.aspects.RevocationsMetricAspect;

import com.codahale.metrics.MetricRegistry;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfig {

    @Bean
    public RevocationsMetricAspect revocationMetricsAspect(MetricRegistry metricRegistry) {
        return new RevocationsMetricAspect(metricRegistry);
    }

}
