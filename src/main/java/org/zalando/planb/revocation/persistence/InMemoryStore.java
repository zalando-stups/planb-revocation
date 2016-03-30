package org.zalando.planb.revocation.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.RevocationRequest;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * Created by jmussler on 12.02.16.
 */
public class InMemoryStore implements RevocationStore {

    private final List<RevocationRequest> revocations = new ArrayList<>();

    private final LinkedList<Refresh> refreshNotifications = new LinkedList<>();

    @Override
    public Collection<RevocationRequest> getRevocations(final int from) {
        return revocations.stream().filter(x -> x.getRevokedAt() > from).collect(Collectors.toList());
    }

    @Override
    public boolean storeRevocation(final RevocationData revocation) {
        final RevocationRequest revocationRequest = new RevocationRequest(revocation.getType(), revocation.getData(), UnixTimestamp.now());
        return revocations.add(revocationRequest);
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
