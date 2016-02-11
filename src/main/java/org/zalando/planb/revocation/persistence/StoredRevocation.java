package org.zalando.planb.revocation.persistence;

/**
 * Created by jmussler on 11.02.16.
 */
public class StoredRevocation {
    String type;
    RevocationData data;

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
}
