package org.zalando.planb.revocation.persistence;

import org.zalando.planb.revocation.domain.ImmutableRefresh;
import org.zalando.planb.revocation.domain.ImmutableRevocationData;
import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.RevocationRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jmussler on 12.02.16.
 */
public class InMemoryStore implements RevocationStore {

    private final List<RevocationData> revocationList = new ArrayList<>();

    private final LinkedList<Refresh> refreshList = new LinkedList<>();

    @Override
    public Collection<RevocationData> getRevocations(final int from) {
        return revocationList.stream().filter(x -> x.revokedAt() > from).collect(Collectors.toList());
    }

    @Override
    public void storeRevocation(final RevocationRequest revocation) {
        revocationList.add(ImmutableRevocationData.builder().revocationRequest(revocation).build());
    }

    @Override
    public Refresh getRefresh() {
        return refreshList.peekLast();
    }

    @Override
    public void storeRefresh(final int from) {

        refreshList.offer(ImmutableRefresh.builder().refreshFrom(from).build());
    }
}
