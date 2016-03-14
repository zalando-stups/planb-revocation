package org.zalando.planb.revocation.persistence;

import java.util.Collection;

import org.zalando.planb.revocation.domain.Refresh;

/**
 * Created by jmussler on 11.02.16.
 */
public interface RevocationStore {

    Collection<StoredRevocation> getRevocations(int from);

    boolean storeRevocation(StoredRevocation revocation);

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
