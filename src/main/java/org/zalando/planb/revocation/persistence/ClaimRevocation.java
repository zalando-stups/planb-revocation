package org.zalando.planb.revocation.persistence;

/**
 * Created by jmussler on 11.02.16.
 */
public class ClaimRevocation extends RevocationData {
    public String hashedClaim;
    public long issued_before;

    public ClaimRevocation(String hashedClaim, long issued_before) {
        this.hashedClaim = hashedClaim;
        this.issued_before = issued_before;
    }

    public long getIssued_before() {
        return issued_before;
    }

    public void setIssued_before(long issued_before) {
        this.issued_before = issued_before;
    }

    public String getHashedClaim() {
        return hashedClaim;
    }

    public void setHashedClaim(String hashedClaim) {
        this.hashedClaim = hashedClaim;
    }
}
