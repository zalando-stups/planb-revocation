package org.zalando.planb.revocation.util.security;

import org.springframework.security.test.context.support.WithSecurityContext;
import org.zalando.planb.revocation.domain.CurrentUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A mock custom user.
 *
 * <p>Use this annotation when you need a mock {@code SecurityContext}, so that a realm and uid are available when
 * invoked in {@link CurrentUser#get()}</p>
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    /** uid of the mock user. Defaults to {@code test0} */
    String uid() default "test0";

    /** uid of the mock user. Defaults to {@code services} */
    String realm() default "/services";
}
