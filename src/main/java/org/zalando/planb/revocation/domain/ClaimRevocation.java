package org.zalando.planb.revocation.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Getter
@Builder
public class ClaimRevocation implements RevocationData {
    private String name;

    private String valueHash;

    private Long issuedBefore;
}
