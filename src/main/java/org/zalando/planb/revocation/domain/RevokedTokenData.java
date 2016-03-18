package org.zalando.planb.revocation.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <ul>
 *     <li>{@code token}: the token to revoke.</li>
 * </ul>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Data
@NoArgsConstructor
public class RevokedTokenData implements RevokedData {

    private String token;
}
