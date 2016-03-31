package org.zalando.planb.revocation.config;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.zalando.planb.revocation.AbstractSpringTest;

import com.datastax.driver.core.policies.AddressTranslator;

@ContextConfiguration(classes = AwsConfig.class)
public class AwsConfigDisabledTest extends AbstractSpringTest {

    @Autowired
    private Optional<AddressTranslator> addressTranslator;

    @Test
    public void testAddressTranslator() {
        Assertions.assertThat(addressTranslator).isEmpty();
    }

}
