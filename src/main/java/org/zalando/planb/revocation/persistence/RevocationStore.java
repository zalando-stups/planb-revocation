package org.zalando.planb.revocation.persistence;

import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.RevocationRequest;

import java.util.Collection;

/**
 * Created by jmussler on 11.02.16.
 */
public interface RevocationStore {

    Collection<RevocationData> getRevocations(int from);

    /**
     * Stores the specified revocation data into the store.
     *
     * @param revocation the revocation to store
     */
    void storeRevocation(RevocationRequest revocation);

    /**
     * Returns the latest refresh notification.
     *
     * @return the latest refresh notification.
     */
    Refresh getRefresh();

    /**
     * Stores the specified timestamp as a refresh notification.
     * <p>
     * <p>The {@link Refresh} object stored will be the latest in the list of refresh notifications.</p>
     *
     * @param from UTC UNIX timestamp from when to refresh revocations.
     */
    void storeRefresh(int from);
}
