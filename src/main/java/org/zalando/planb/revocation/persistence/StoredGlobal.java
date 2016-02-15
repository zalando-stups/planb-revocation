package org.zalando.planb.revocation.persistence;

/**
 * Created by jmussler on 11.02.16.
 */
public class StoredGlobal extends RevocationData {
    long issued_before;

    public StoredGlobal(long issued_before) {
        this.issued_before = issued_before;
    }

    public long getIssued_before() {
        return issued_before;
    }

    public void setIssued_before(long issued_before) {
        this.issued_before = issued_before;
    }
}
