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
public class RevocationRequest extends RevocationData {

    @JsonProperty("revoked_at")
    private Integer revokedAt = UnixTimestamp.now();

    public RevocationRequest(RevocationType type, RevokedData data, Integer revokedAt) {
        this.setType(type);
        this.setData(data);
        this.revokedAt = revokedAt;
    }

}
