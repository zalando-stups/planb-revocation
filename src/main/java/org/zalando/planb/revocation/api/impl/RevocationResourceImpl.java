package org.zalando.planb.revocation.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

import org.zalando.planb.revocation.api.RevocationResource;
import org.zalando.planb.revocation.domain.ClaimRevocation;
import org.zalando.planb.revocation.domain.GlobalRevocation;
import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.TokenRevocation;
import org.zalando.planb.revocation.persistence.RevocationData;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.persistence.StoredClaim;
import org.zalando.planb.revocation.persistence.StoredGlobal;
import org.zalando.planb.revocation.persistence.StoredRevocation;
import org.zalando.planb.revocation.persistence.StoredToken;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@RestController
public class RevocationResourceImpl implements RevocationResource {

    @Autowired
    RevocationStore storage;

    @Override
    public HttpEntity<RevocationInfo> get(final long from) {
        Collection<StoredRevocation> revocations = storage.getRevocations(from);

        List<Revocation> apiRevocations = new ArrayList<>(revocations.size());
        for (StoredRevocation stored : revocations) {
            final RevocationData data = stored.getData();

            org.zalando.planb.revocation.domain.RevocationData apiData = null;
            if (data instanceof StoredGlobal) {
                apiData = GlobalRevocation.builder().issuedBefore(((StoredGlobal) data).getIssued_before()).build();
            } else if (data instanceof StoredClaim) {

                // here hash from stored value to hashed value sha-2
                throw new NotImplementedException();
            } else if (data instanceof StoredToken) {
                apiData = TokenRevocation.builder().tokenHash(((StoredToken) data).getTokenHash()).build();
            }

            Revocation revocation = Revocation.builder().data(apiData).revokedAt(stored.getRevokedAt())
                                              .type(stored.getType()).build();
            apiRevocations.add(revocation);
        }

        RevocationInfo wrapper = RevocationInfo.builder().revocations(apiRevocations).build();
        return new ResponseEntity<>(wrapper, HttpStatus.OK);
    }

    @Override
    public HttpEntity<String> post(final Revocation r) {
        RevocationData data = null;
        switch (r.getType()) {

            case CLAIM : {
                ClaimRevocation cr = (ClaimRevocation) r.getData();

                // hash value is not hashed on the way in
                data = new StoredClaim(cr.getName(), cr.getValueHash(), cr.getIssuedBefore());
                break;
            }

            case TOKEN : {
                TokenRevocation tr = (TokenRevocation) r.getData();
                data = new StoredToken(tr.getTokenHash());
                break;
            }

            case GLOBAL : {
                GlobalRevocation gr = (GlobalRevocation) r.getData();
                data = new StoredGlobal(gr.getIssuedBefore());
                break;
            }
        }

        if (null == data) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        StoredRevocation storedRevocation = new StoredRevocation(data, r.getType(), "<!--add revoked by-->");
        if (storage.storeRevocation(storedRevocation)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
