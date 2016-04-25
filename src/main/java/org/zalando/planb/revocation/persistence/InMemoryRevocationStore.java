package org.zalando.planb.revocation.persistence;

import org.slf4j.Logger;
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

import static org.slf4j.LoggerFactory.getLogger;

public class InMemoryRevocationStore implements RevocationStore {

    private final Logger log = getLogger(getClass());

    private final List<RevocationData> revocations = new ArrayList<>();

    private final LinkedList<Refresh> refreshNotifications = new LinkedList<>();

    @Override
    public Collection<RevocationData> getRevocations(final int from) {
        return revocations.stream().filter(x -> x.revokedAt() > from).collect(Collectors.toList());
    }

    @Override
    public void storeRevocation(final RevocationRequest revocation) {
        final RevocationData revocationData = ImmutableRevocationData.builder().revocationRequest(revocation).build();
        log.debug("Store revocation in memory: {}", revocationData);
        revocations.add(revocationData);
    }

    @Override
    public Refresh getRefresh() {
        return refreshNotifications.peekLast();
    }

    @Override
    public void storeRefresh(final int from) {
        final Refresh refreshNotification = ImmutableRefresh.builder().refreshFrom(from).build();
        log.debug("Store refresh in memory: {}", refreshNotification);
        refreshNotifications.offer(refreshNotification);
    }
}
