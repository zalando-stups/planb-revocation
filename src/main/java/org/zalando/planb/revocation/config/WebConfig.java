package org.zalando.planb.revocation.config;

import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.zalando.planb.revocation.api.impl.ResponseSizeHandlerInterceptor;
import org.zalando.planb.revocation.web.RequestInfoMDCFilter;

@Configuration
@ComponentScan(
        useDefaultFilters = false,
        includeFilters = @Filter(classes = {Controller.class, RestController.class, ControllerAdvice.class}),
        basePackages = "org.zalando.planb.revocation.api")
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

    @Bean
    public RequestInfoMDCFilter requestInfoMDCFilter() {
        return new RequestInfoMDCFilter();
    }
}
