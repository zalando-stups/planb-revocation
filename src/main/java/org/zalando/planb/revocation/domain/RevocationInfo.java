package org.zalando.planb.revocation.domain;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Data
@Builder
public class RevocationInfo {

    private String meta;

    private List<Revocation> revocations;
}
