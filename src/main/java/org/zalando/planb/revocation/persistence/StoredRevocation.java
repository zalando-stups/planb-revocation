package org.zalando.planb.revocation.persistence;

/**
 * Created by jmussler on 11.02.16.
 */
public class StoredRevocation {
    String type;
    RevocationData data;
    Long revocedAt;
    String revocedBy;

    public StoredRevocation(RevocationData data, String type, String revokedBy) {
        this.data = data;

        if(!type.equals(type.toUpperCase())) {
            throw new IllegalArgumentException();
        }

        this.type = type;
        this.revocedBy = revokedBy;
        this.revocedAt = System.currentTimeMillis();
    }

    public Long getRevocedAt() {
        return revocedAt;
    }

    public void setRevocedAt(Long revocedAt) {
        this.revocedAt = revocedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RevocationData getData() {
        return data;
    }

    public void setData(RevocationData data) {
        this.data = data;
    }

    public String getRevocedBy() {
        return revocedBy;
    }

    public void setRevocedBy(String revocedBy) {
        this.revocedBy = revocedBy;
    }
}
