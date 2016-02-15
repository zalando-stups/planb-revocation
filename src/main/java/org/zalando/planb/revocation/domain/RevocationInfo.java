package org.zalando.planb.revocation.domain;

import java.util.EnumMap;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@Getter
@Builder
public class RevocationInfo {

    private EnumMap<RevocationFlags, String> meta;

    private List<Revocation> revocations;
}
