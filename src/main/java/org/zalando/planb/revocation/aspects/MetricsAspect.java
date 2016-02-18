package org.zalando.planb.revocation.aspects;

import com.codahale.metrics.MetricRegistry;


public abstract class MetricsAspect {

    protected MetricRegistry metricRegistry;

    public MetricsAspect(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

}
