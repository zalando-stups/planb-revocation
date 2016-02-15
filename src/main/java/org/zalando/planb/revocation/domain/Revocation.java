package org.zalando.planb.revocation.domain;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Getter
@Builder
public class Revocation {

    private RevocationType type;

    private Date revokedAt;

    private List<RevocationData> data;
}
