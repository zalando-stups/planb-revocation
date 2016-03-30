package org.zalando.planb.revocation.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.StoredRevocationData;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * Created by jmussler on 12.02.16.
 */
public class InMemoryStore implements RevocationStore {

    private final List<StoredRevocationData> revocations = new ArrayList<>();

    private final LinkedList<Refresh> refreshNotifications = new LinkedList<>();

    @Override
    public Collection<StoredRevocationData> getRevocations(final int from) {
        return revocations.stream().filter(x -> x.getRevokedAt() > from).collect(Collectors.toList());
    }

    @Override
    public boolean storeRevocation(final RevocationData revocation) {
        final StoredRevocationData storedRevocationData = new StoredRevocationData(revocation.getType(), revocation.getData(), UnixTimestamp.now());
        return revocations.add(storedRevocationData);
    }

    @Override
    public Refresh getRefresh() {
        return refreshNotifications.peekLast();
    }

    @Override
    public boolean storeRefresh(final int from) {
        return refreshNotifications.offer(Refresh.create(from));
    }
}
