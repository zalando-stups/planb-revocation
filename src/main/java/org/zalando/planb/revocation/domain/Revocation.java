package org.zalando.planb.revocation.domain;

import java.util.Date;
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
public class Revocation {

    private RevocationType type;

    private Date revocatedAt;

    private List<String> data;
}
