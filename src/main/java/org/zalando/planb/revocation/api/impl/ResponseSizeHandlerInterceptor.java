package org.zalando.planb.revocation.api.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.zalando.planb.revocation.config.WebConfig;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;

/**
 * Will be registered with {@link WebConfig#handlerInterceptorConfigurer()}.
 * 
 * @author jbellmann
 *
 */
public class ResponseSizeHandlerInterceptor extends HandlerInterceptorAdapter {

    private final Histogram histogram;

    public ResponseSizeHandlerInterceptor(MetricRegistry metricRegistry) {
        this.histogram = metricRegistry.histogram("planb.revocations.responseSize");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        if (HttpMethod.GET.name().equals(request.getMethod())) {
            String headerValue = response.getHeader(HttpHeaders.CONTENT_LENGTH);
            if (headerValue != null) {
                histogram.update(Long.valueOf(headerValue));
            }
        }
    }

}
