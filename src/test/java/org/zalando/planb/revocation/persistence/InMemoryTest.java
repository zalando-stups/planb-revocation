package org.zalando.planb.revocation.persistence;

import org.springframework.test.context.ActiveProfiles;

/**
 * Implementation class for In Memory store tests.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@ActiveProfiles("test")
public class InMemoryTest extends AbstractStoreTests { }
