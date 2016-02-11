package org.zalando.planb.revocation.persistence;

/**
 * Created by jmussler on 11.02.16.
 */
public class GlobalRevocation extends RevocationData {
    long issued_before;

    public GlobalRevocation(long issued_before) {
        this.issued_before = issued_before;
    }

    public long getIssued_before() {
        return issued_before;
    }

    public void setIssued_before(long issued_before) {
        this.issued_before = issued_before;
    }
}
