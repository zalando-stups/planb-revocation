package exclude.from.componentscan;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.config.properties.RevocationProperties;
import org.zalando.planb.revocation.service.RevocationAuthorizationService;
import org.zalando.planb.revocation.util.security.ClaimRevocationAlwaysAuthorizedService;

@Configuration
public class NoopRevocationAuthorizationConfig {

    @Bean
    @Primary
    public RevocationAuthorizationService revocationAlwaysAuthorizedService(
            final RevocationProperties revocationProperties,
            final CassandraProperties cassandraProperties) {
        return new ClaimRevocationAlwaysAuthorizedService(revocationProperties, cassandraProperties);
    }
}
