package org.zalando.planb.revocation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.zalando.planb.revocation.config.properties.SecurityProperties;
import org.zalando.stups.oauth2.spring.security.expression.ExtendedOAuth2WebSecurityExpressionHandler;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;

import java.util.concurrent.TimeUnit;

/**
 * @author jbellmann
 */
@Configuration
@EnableResourceServer
@EnableConfigurationProperties(SecurityProperties.class)
@Slf4j
public class SecurityConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Bean
    public ResourceServerTokenServices tokenInfoTokenServices() {
        return new TokenInfoResourceServerTokenServices(securityProperties.getTokenInfoUri());
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // add support for #oauth2.hasRealm() expressions
        resources.resourceId("revocation").expressionHandler(new ExtendedOAuth2WebSecurityExpressionHandler());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.headers().defaultsDisabled().httpStrictTransportSecurity()
                .and().addHeaderWriter(hstsHeaderWriter())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and().httpBasic().disable().anonymous().disable()
                .authorizeRequests().antMatchers(HttpMethod.POST, "/**").access(securityProperties.getRevoke());
    }

    private HstsHeaderWriter hstsHeaderWriter() {
        return new HstsHeaderWriter(AnyRequestMatcher.INSTANCE, TimeUnit.DAYS.toSeconds(365), true);
    }
}
