package org.zalando.planb.revocation.persistence;

import java.util.Collection;

/**
 * Created by jmussler on 11.02.16.
 */
public class CassandraStorage implements RevocationStore {
    @Override
    public Collection<StoredRevocation> getRevocations() {
        return null;
    }

    @Override
    public Collection<StoredRevocation> getRevocations(final long from) {
        return null;
    }

    @Override
    public boolean storeRevocation(StoredRevocation revocation) {

    }
}
