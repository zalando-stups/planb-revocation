package org.zalando.planb.revocation.aspects;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.zalando.planb.revocation.domain.ImmutableRevocationInfo;
import org.zalando.planb.revocation.domain.ImmutableRevokedClaimsInfo;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationType;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.zalando.planb.revocation.util.InstantTimestamp;
import org.zalando.planb.revocation.util.UnixTimestamp;

public class MetricsAspectTest {

    private static final String UNKNOWN = "unknown";

    private final static String[] NAMES = {"uid", "azp"};

    private final static String VALUE_HASH = "CDUg1ANEiZnh5rGFNqUiU4d5TrbtwLNkOgtpjSu3B0s=";

    private final static String HASH_ALGORITHM = "SHA-256";

    private final static Character SEPARATOR = '|';

    private final static Integer ISSUED_BEFORE = InstantTimestamp.FIVE_MINUTES_AGO.seconds();

    @Test
    public void testHelperMethod() {
        RevocationsMetricAspect aspect = new RevocationsMetricAspect(Mockito.mock(MetricRegistry.class));
        Assertions.assertThat(aspect.revocationType(null)).isEqualTo(UNKNOWN);

        Assertions.assertThat(aspect.revocationType(generateRevocationInfo())).isEqualTo("claim");
    }

    @Test
    public void testSuccessType() {
        RevocationsMetricAspect aspect = new RevocationsMetricAspect(Mockito.mock(MetricRegistry.class));
        Assertions.assertThat(aspect.successType(true)).isEqualTo("success");
        Assertions.assertThat(aspect.successType(false)).isEqualTo("failure");
    }

    @Test
    public void testBuildKey() {
        RevocationsMetricAspect aspect = new RevocationsMetricAspect(Mockito.mock(MetricRegistry.class));
        String key = aspect.buildKey("claim", false);
        Assertions.assertThat(key).isEqualTo("planb.revocations.claim.failure");
    }

    @Test(expected = RuntimeException.class)
    public void exceptionOnProceed() throws Throwable {
        RevocationsMetricAspect aspect = new RevocationsMetricAspect(Mockito.mock(MetricRegistry.class));
        RevocationInfo revocation = generateRevocationInfo();
        ProceedingJoinPoint pjp = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(pjp.proceed(Mockito.any())).thenThrow(new RuntimeException("TEST"));
        aspect.around(pjp, revocation);
    }

    @Test
    public void timerWritenOnExceptionOnProceed() throws Throwable {
        MetricRegistry registry = new MetricRegistry();
        RevocationsMetricAspect aspect = new RevocationsMetricAspect(registry);
        RevocationInfo revocation = generateRevocationInfo();
        ProceedingJoinPoint pjp = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(pjp.proceed(Mockito.any())).thenThrow(new RuntimeException("TEST"));
        try{
            
            aspect.around(pjp, revocation);
            Assertions.fail("SHOULD RAISE AN ERROR");
        } catch (RuntimeException e) {
            Map<String, Timer> timers = registry.getTimers();
            Assertions.assertThat(timers.get("planb.revocations.claim.failure")).isNotNull();
        }
    }

    public static RevocationInfo generateRevocationInfo() {
        return ImmutableRevocationInfo.builder()
                .type(RevocationType.CLAIM)
                .revokedAt(UnixTimestamp.now())
                .data(ImmutableRevokedClaimsInfo.builder()
                        .addNames(NAMES)
                        .valueHash(VALUE_HASH)
                        .hashAlgorithm(HASH_ALGORITHM)
                        .separator(SEPARATOR)
                        .issuedBefore(ISSUED_BEFORE)
                        .build())
                .build();
    }
}
