package org.zalando.planb.revocation.persistence;

import java.util.*;
import java.util.stream.Collectors;

import org.zalando.planb.revocation.domain.AuthorizationRule;
import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.RevocationRequest;
import org.zalando.planb.revocation.util.UnixTimestamp;

public class InMemoryStore implements RevocationStore, AuthorizationRulesStore {

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

    @Override
    public Collection<AuthorizationRule> getAccessList(AuthorizationRule authorizationRule) {
        return Collections.emptyList();
    }

    @Override
    public boolean storeAccessRule(AuthorizationRule authorizationRule) {
        return false;
    }
}
