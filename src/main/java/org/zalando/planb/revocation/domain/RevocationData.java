package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Setter
@Getter
@NoArgsConstructor
public class RevocationData extends RevocationRequest {

    @JsonProperty("revoked_at")
    private Integer revokedAt = UnixTimestamp.now();

    public RevocationData(RevocationType type, RevokedData data, Integer revokedAt) {
        super(type, data);
        this.setRevokedAt(revokedAt);
    }
}
