package org.zalando.planb.revocation.config.properties;

import com.google.common.base.Preconditions;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Contains properties used for configuring OAuth 2 scopes for REST operations.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@ConfigurationProperties(prefix = "api.security")
public class ApiSecurityProperties {

    private String revokeExpr;

    public String getRevokeExpr() {
        return revokeExpr;
    }

    public void setRevokeExpr(String revokeExpr) {
        this.revokeExpr = Preconditions.checkNotNull(revokeExpr, "api.security.revokeExpr cannot be 'null'");
    }
}