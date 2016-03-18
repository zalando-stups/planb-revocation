package org.zalando.planb.revocation.persistence;

import java.util.Collection;

import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.domain.RevocationData;

/**
 * Created by jmussler on 11.02.16.
 */
public interface RevocationStore {

    Collection<RevocationData> getRevocations(int from);

    /**
     * Stores the specified revocation data into the store.
     *
     * @param revocation    the revocation to store
     * @return  {@code true} if the opertion was successful, {@code false} otherwise
     */
    boolean storeRevocation(RevocationData revocation);

    /**
     * Returns the latest refresh notification.
     *
     * @return  the latest refresh notification.
     */
    Refresh getRefresh();

    /**
     * Stores the specified timestamp as a refresh notification.
     *
     * <p>The {@link Refresh} object stored will be the latest in the list of refresh notifications.</p>
     *
     * @param   from  UTC UNIX timestamp from when to refresh revocations.
     *
     * @return  {@code true} if the operation was successful, {@code false} otherwise.
     */
    boolean storeRefresh(int from);
}
