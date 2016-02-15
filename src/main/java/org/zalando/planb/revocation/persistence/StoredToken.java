package org.zalando.planb.revocation.persistence;

/**
 * Created by jmussler on 11.02.16.
 */
public class StoredToken extends RevocationData {
    String tokenHash;

    public StoredToken(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }
}
