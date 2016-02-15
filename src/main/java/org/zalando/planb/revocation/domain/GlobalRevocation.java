package org.zalando.planb.revocation.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Getter
@Builder
public class GlobalRevocation implements RevocationData {

    private Instant issuedBefore;
}
