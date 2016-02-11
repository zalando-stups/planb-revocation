package org.zalando.planb.revocation.persistence;

/**
 * Created by jmussler on 11.02.16.
 */
public class TokenRevocation extends RevocationData {
    String tokenHash;

    public TokenRevocation(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }
}
