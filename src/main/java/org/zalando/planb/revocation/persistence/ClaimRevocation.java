package org.zalando.planb.revocation.persistence;

/**
 * Created by jmussler on 11.02.16.
 */
public class ClaimRevocation extends RevocationData {
    // attribute name
    public String claimName;
    public String claimValue;
    public long issuedBefore;

    public ClaimRevocation(String claimName, String claimValue, long issuedBefore) {
        this.claimName = claimName;
        this.claimValue = claimValue;
        this.issuedBefore = issuedBefore;
    }

    public String getClaimName() {
        return claimName;
    }

    public void setClaimName(String claimName) {
        this.claimName = claimName;
    }

    public String getClaimValue() {
        return claimValue;
    }

    public void setClaimValue(String claimValue) {
        this.claimValue = claimValue;
    }

    public long getIssuedBefore() {
        return issuedBefore;
    }

    public void setIssuedBefore(long issuedBefore) {
        this.issuedBefore = issuedBefore;
    }
}
