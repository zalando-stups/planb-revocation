package org.zalando.planb.revocation.aspects;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.StopWatch;
import org.zalando.planb.revocation.domain.RevocationInfo;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

@Aspect
public class RevocationsMetricAspect extends MetricsAspect {

    private static final String UNKNOWN = "unknown";

    static final String PREFIX = "planb.revocations";
    static final String DOT = ".";

    public RevocationsMetricAspect(MetricRegistry registry) {
        super(registry);
    }

    @Around("execution (* org.zalando.planb.revocation.api.impl.RevocationResourceImpl.post(*)) && args(revocation)")
    public Object around(ProceedingJoinPoint pjp, RevocationInfo revocation) throws Throwable {
        Object retval = null;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean success = true;
        try {
            retval = pjp.proceed(new Object[] { revocation });
            return retval;
        } catch (Throwable t) {
            success = false;
            throw t;
        } finally {
            stopWatch.stop();
            long time = stopWatch.getLastTaskTimeMillis();
            Timer timer = metricRegistry.timer(buildKey(revocationType(revocation), success));
            timer.update(time, TimeUnit.MILLISECONDS);
        }
    }

    protected String revocationType(RevocationInfo revocation) {
        if (revocation == null) {
            return UNKNOWN;
        } else {
            if (revocation.getType() == null) {
                return UNKNOWN;
            }else{
                return revocation.getType().toString().toLowerCase();
            }
        }
    }

    protected String buildKey(String tokenType, boolean success){
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX).append(DOT).append(tokenType).append(DOT).append(successType(success));
        return sb.toString();
    }

    protected String successType(boolean success) {
        return success ? "success" : "failure";
    }
}
