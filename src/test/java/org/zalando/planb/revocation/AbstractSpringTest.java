package org.zalando.planb.revocation;

import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

/**
 * @author jbellmann
 */
public abstract class AbstractSpringTest extends AbstractOAuthTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public SpringMethodRule methodRule = new SpringMethodRule();

}
