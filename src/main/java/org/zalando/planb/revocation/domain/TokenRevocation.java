package org.zalando.planb.revocation.domain;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Getter
@Builder
public class TokenRevocation implements RevocationData {
    private String tokenHash;

    private Instant revokedAt;
}
