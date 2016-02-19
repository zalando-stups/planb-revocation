package org.zalando.planb.revocation.aspects;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationType;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class MetricsAspectTest {

    private static final String UNKNOWN = "unknown";

    @Test
    public void testHelperMethod() {
        RevocationsMetricAspect aspect = new RevocationsMetricAspect(Mockito.mock(MetricRegistry.class));
        Assertions.assertThat(aspect.revocationType(null)).isEqualTo(UNKNOWN);
        //
        Revocation revocation = new Revocation();
        Assertions.assertThat(aspect.revocationType(revocation)).isEqualTo(UNKNOWN);

        Revocation revocation2 = new Revocation();
        revocation2.setType(RevocationType.CLAIM);
        Assertions.assertThat(aspect.revocationType(revocation2)).isEqualTo("claim");
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
        Revocation revocation = new Revocation();
        revocation.setType(RevocationType.CLAIM);
        ProceedingJoinPoint pjp = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(pjp.proceed(Mockito.any())).thenThrow(new RuntimeException("TEST"));
        aspect.around(pjp, revocation);
    }

    @Test
    public void timerWritenOnExceptionOnProceed() throws Throwable {
        MetricRegistry registry = new MetricRegistry();
        RevocationsMetricAspect aspect = new RevocationsMetricAspect(registry);
        Revocation revocation = new Revocation();
        revocation.setType(RevocationType.CLAIM);
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
}
