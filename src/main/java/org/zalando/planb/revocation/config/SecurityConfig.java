package org.zalando.planb.revocation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
import org.zalando.planb.revocation.domain.CurrentUser;
import org.zalando.stups.oauth2.spring.security.expression.ExtendedOAuth2WebSecurityExpressionHandler;
import org.zalando.stups.oauth2.spring.server.DefaultAuthenticationExtractor;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;

import java.util.concurrent.TimeUnit;

/**
 * @author jbellmann
 */
@Configuration
@EnableResourceServer
@EnableConfigurationProperties(ApiSecurityProperties.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter implements ResourceServerConfigurer {

    @Autowired
    private ApiSecurityProperties apiSecurityProperties;

    @Autowired
    private ResourceServerProperties resourceServerProperties;

    @Bean
    public ResourceServerTokenServices tokenInfoTokenServices() {
        return new TokenInfoResourceServerTokenServices("CLIENT_ID_NOT_NEEDED",
                new DefaultAuthenticationExtractor(),
                new FallbackTokenInfoRequestExecutor(resourceServerProperties.getTokenInfoUri()));
    }

    @Override
    public void configure(final ResourceServerSecurityConfigurer resources) throws Exception {

        // add support for #oauth2.hasRealm() expressions
        resources.resourceId("revocation").expressionHandler(new ExtendedOAuth2WebSecurityExpressionHandler());
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/swagger.json", "/.well-known/schema-discovery").antMatchers(HttpMethod.GET,
                "/revocations/**");
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {

        http.headers().defaultsDisabled().httpStrictTransportSecurity().and().addHeaderWriter(hstsHeaderWriter()).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().httpBasic().disable()
                .anonymous().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/revocations")
                    .access(apiSecurityProperties.getRevokeExpr())
                .antMatchers(HttpMethod.GET, "/revocations").permitAll()
                .antMatchers(HttpMethod.POST, "/notifications/**")
                .access(apiSecurityProperties.getRevokeExpr())
                .anyRequest().denyAll();
    }

    private HstsHeaderWriter hstsHeaderWriter() {
        return new HstsHeaderWriter(AnyRequestMatcher.INSTANCE, TimeUnit.DAYS.toSeconds(365), true);
    }

    @Bean
    public CurrentUser currentUser(){
        return new CurrentUser();
    }
}
