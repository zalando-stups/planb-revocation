package org.zalando.planb.revocation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.http.OAuth2ErrorHandler;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.DefaultOAuth2ExceptionRenderer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.zalando.stups.oauth2.spring.security.expression.ExtendedOAuth2WebSecurityExpressionHandler;
import org.zalando.stups.oauth2.spring.server.AuthenticationExtractor;
import org.zalando.stups.oauth2.spring.server.DefaultAuthenticationExtractor;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;

import java.util.concurrent.TimeUnit;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
@EnableWebSecurity
@EnableResourceServer
public class SecurityConfig extends WebSecurityConfigurerAdapter implements ResourceServerConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    private static final String OAUTH2_CLIENT_ID = "zmon-data-service";

    @Autowired
    private PlanBRevocationConfig config;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        OAuth2ExceptionRenderer exceptionRenderer = new DefaultOAuth2ExceptionRenderer();

        final OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        authenticationEntryPoint.setExceptionRenderer(exceptionRenderer);

        final OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
        accessDeniedHandler.setExceptionRenderer(exceptionRenderer);

        resources.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler);

        // here is the important part
        resources.expressionHandler(new ExtendedOAuth2WebSecurityExpressionHandler());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/health");
    }

    //@formatter:off
    @Override
    public void configure(HttpSecurity http) throws Exception {
        LOG.info("Using the following oauth2 constraints:");
        LOG.info("    getApi: {}", config.getOauth2Scopes().get("readApi"));
        LOG.info("    writeApi: {}", config.getOauth2Scopes().get("writeApi"));
        LOG.info("    revokeStandard: {}", config.getOauth2Scopes().get("revokeStandard"));

        http
            .headers()
                .defaultsDisabled()
                .httpStrictTransportSecurity()
            .and()
                .addHeaderWriter(hstsHeaderWriter())
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .and()
                .httpBasic()
                    .disable()
                .anonymous()
                    .disable()
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/**").access(config.getOauth2Scopes().get("readApi"))
                    .antMatchers(HttpMethod.POST, "/**").access(config.getOauth2Scopes().get("write"))
                    .antMatchers(HttpMethod.POST, "/revoke").access(config.getOauth2Scopes().get("revokeStandard"))
                    // deny anything else to avoid opening up other APIs by mistake!!
                    .anyRequest().denyAll();
    }

    //@formatter:on

    @Bean
    public ResourceServerTokenServices resourceServerTokenServices(final RestTemplate tokenInfo) {
        String endpoint = config.getOauth2TokenInfoUrl();

        final AuthenticationExtractor authenticationExtractor = new DefaultAuthenticationExtractor();

        return new TokenInfoResourceServerTokenServices(endpoint, OAUTH2_CLIENT_ID, authenticationExtractor,
                tokenInfo);
    }

    @Bean
    public RestTemplate tokenInfo(final ClientHttpRequestFactory requestFactory,
            final HttpMessageConverters messageConverters) {
        final RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setMessageConverters(messageConverters.getConverters());

        final BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
        resource.setClientId(OAUTH2_CLIENT_ID);
        restTemplate.setErrorHandler(new OAuth2ErrorHandler(resource));

        return restTemplate;
    }

    @Bean
    ClientHttpRequestFactory requestFactory() {
        // TODO, we use 'http.maxConnection' as systemProperties
        // CloseableHttpClient httpClient =
        // HttpClientBuilder.create().useSystemProperties().setMaxConnTotal(200)
        // .setMaxConnPerRoute(200).build();

        final HttpComponentsClientHttpRequestFactory httpClientFactory = new HttpComponentsClientHttpRequestFactory();
        httpClientFactory.setConnectTimeout(2000);
        httpClientFactory.setReadTimeout(2000);
        return httpClientFactory;
    }

    private HstsHeaderWriter hstsHeaderWriter() {
        return new HstsHeaderWriter(AnyRequestMatcher.INSTANCE, TimeUnit.DAYS.toSeconds(365), true);
    }

}
