package org.zalando.planb.revocation.domain;

import java.util.EnumMap;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Data
@NoArgsConstructor
public class RevocationInfo {

    private EnumMap<NotificationType, Object> meta;

    private List<Revocation> revocations;
}
