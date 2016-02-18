package org.zalando.planb.revocation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.zalando.planb.revocation.api.impl.ResponseSizeHandlerInterceptor;

import com.codahale.metrics.MetricRegistry;

@Configuration
public class WebConfig {

    @Autowired
    private MetricRegistry metricRegistry;

    @Bean
    public WebMvcConfigurer handlerInterceptorConfigurer() {
        return new WebMvcConfigurerAdapter() {

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new ResponseSizeHandlerInterceptor(metricRegistry))
                        .addPathPatterns("/revocations");
            }
        };
    }
}
