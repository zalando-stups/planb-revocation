package org.zalando.planb.revocation.config;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.zalando.planb.revocation.config.properties.ApiSecurityProperties;
import org.zalando.stups.oauth2.spring.security.expression.ExtendedOAuth2WebSecurityExpressionHandler;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
@EnableResourceServer
public class SecurityConfig extends WebSecurityConfigurerAdapter implements ResourceServerConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private ApiSecurityProperties apiSecurityProperties;

    @Autowired
    private ResourceServerProperties resourceServerProperties;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // here is the important part
        resources.expressionHandler(new ExtendedOAuth2WebSecurityExpressionHandler());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/health", "/swagger.json");
    }

    //@formatter:off
    @Override
    public void configure(HttpSecurity http) throws Exception {
        LOG.info("Using the following oauth2 constraints:");
        LOG.info("    getApi: {}", apiSecurityProperties.getOauth2Scopes().get("readApi"));
        LOG.info("    writeApi: {}", apiSecurityProperties.getOauth2Scopes().get("writeApi"));
        LOG.info("    revokeStandard: {}", apiSecurityProperties.getOauth2Scopes().get("revokeStandard"));

        http
            .headers()
                .defaultsDisabled()
                .httpStrictTransportSecurity()
            .and()
                .addHeaderWriter(hstsHeaderWriter())
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .httpBasic()
                    .disable()
                .anonymous()
                    .disable()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/revoke").access(apiSecurityProperties.getOauth2Scopes().get("revokeStandard"))
                    // 'all'-matchers should always be the last
                    .antMatchers(HttpMethod.GET, "/**").access(apiSecurityProperties.getOauth2Scopes().get("readApi"))
                    .antMatchers(HttpMethod.POST, "/**").access(apiSecurityProperties.getOauth2Scopes().get("write"))
                    // deny anything else to avoid opening up other APIs by mistake!!
                    .anyRequest()
                        .denyAll();
    }

    //@formatter:on

    @Bean
    public ResourceServerTokenServices resourceServerTokenServices() {
        return new TokenInfoResourceServerTokenServices(resourceServerProperties.getTokenInfoUri());
    }

    private HstsHeaderWriter hstsHeaderWriter() {
        return new HstsHeaderWriter(AnyRequestMatcher.INSTANCE, TimeUnit.DAYS.toSeconds(365), true);
    }


}
