package org.zalando.planb.revocation.persistence;

import java.util.Collection;

/**
 * Created by jmussler on 11.02.16.
 */
public interface RevocationStore {
    Collection<StoredRevocation> getRevocations();
    Collection<StoredRevocation> getRevocations(long from);
}
