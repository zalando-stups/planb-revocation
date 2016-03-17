package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * <ul>
 *     <li>{@code token}: the token to revoke.</li>
 * </ul>
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Data
@NoArgsConstructor
public class RevokedTokenData implements Revoked {

    private String token;
}
