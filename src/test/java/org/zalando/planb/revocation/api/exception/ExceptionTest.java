package org.zalando.planb.revocation.api.exception;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ExceptionTest {

    @Test
    public void yamlParsingException() {
        RuntimeException re = new YamlParsingException();
        Assertions.assertThat(re.getMessage()).isEqualTo(YamlParsingException.MESSAGE);
    }

}
