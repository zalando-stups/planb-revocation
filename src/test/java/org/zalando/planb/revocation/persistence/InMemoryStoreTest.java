package org.zalando.planb.revocation.persistence;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.planb.revocation.domain.RevocationRequest;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.RevokedData;
import org.zalando.planb.revocation.domain.RevokedTokenData;
import org.zalando.planb.revocation.util.UnixTimestamp;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Implementation class for In Memory store tests.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@ActiveProfiles("test")
public class InMemoryStoreTest extends AbstractStoreTests {

    @Autowired
    RevocationStore revocationStore;

    @Test
    public void testInMemoryStore() throws InterruptedException {
        int timestamp = UnixTimestamp.now();

        revocationStore.storeRevocation(generateRevocation());

        Collection<RevocationData> revocations = revocationStore.getRevocations(timestamp-100);
        assertThat(revocations.size()).isNotZero();
    }

    private RevocationRequest generateRevocation(){
        RevokedData revokedData = new RevokedTokenData();
        RevocationRequest revocationRequest = new RevocationRequest();
        revocationRequest.setData(revokedData);
        revocationRequest.setType(RevocationType.TOKEN);

        return revocationRequest;
    }
}
