package org.zalando.planb.revocation.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.zalando.planb.revocation.domain.Refresh;

/**
 * Created by jmussler on 12.02.16.
 */
public class InMemoryStore implements RevocationStore {

    private final List<StoredRevocation> revocations = new ArrayList<>();

    private final LinkedList<Refresh> refreshNotifications = new LinkedList<>();

    @Override
    public Collection<StoredRevocation> getRevocations(final int from) {
        return revocations.stream().filter(x -> x.getRevokedAt() > from).collect(Collectors.toList());
    }

    @Override
    public boolean storeRevocation(final StoredRevocation revocation) {
        return revocations.add(revocation);
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
