package org.zalando.planb.revocation.persistence;

import org.zalando.planb.revocation.domain.ImmutableRefresh;
import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.RevocationRequest;
import org.zalando.planb.revocation.util.UnixTimestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryRevocationStore implements RevocationStore {

    private final List<RevocationData> revocations = new ArrayList<>();

    private final LinkedList<Refresh> refreshNotifications = new LinkedList<>();

    @Override
    public Collection<RevocationData> getRevocations(final int from) {
        return revocations.stream().filter(x -> x.getRevokedAt() > from).collect(Collectors.toList());
    }

    @Override
    public void storeRevocation(final RevocationRequest revocation) {
        final RevocationData revocationData = new RevocationData(revocation.getType(), revocation.getData(), UnixTimestamp.now());
        revocations.add(revocationData);
    }

    @Override
    public Refresh getRefresh() {
        return refreshNotifications.peekLast();
    }

    @Override
    public void storeRefresh(final int from) {
        refreshNotifications.offer(ImmutableRefresh.builder().refreshFrom(from).build());
    }



}
