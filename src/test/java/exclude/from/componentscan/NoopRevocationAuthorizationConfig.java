package exclude.from.componentscan;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.zalando.planb.revocation.api.RevocationAlwaysAuthorizedService;
import org.zalando.planb.revocation.service.RevocationAuthorizationService;

@Configuration
public class NoopRevocationAuthorizationConfig {

    @Bean
    @Primary
    public RevocationAuthorizationService revocationAlwaysAuthorizedService() {
        return new RevocationAlwaysAuthorizedService();
    }
}
