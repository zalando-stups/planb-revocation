package org.zalando.planb.revocation.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jmussler on 12.02.16.
 */
public class InMemoryStore implements RevocationStore {

    private List<StoredRevocation> revocations = new ArrayList<>();

    @Override
    public Collection<StoredRevocation> getRevocations(long from) {
        return revocations.stream().filter(x -> x.getRevokedAt() > from).collect(Collectors.toList());
    }

    @Override
    public boolean storeRevocation(StoredRevocation revocation) {
        return revocations.add(revocation);
    }
}
