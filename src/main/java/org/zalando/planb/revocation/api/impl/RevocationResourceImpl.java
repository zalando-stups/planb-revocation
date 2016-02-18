package org.zalando.planb.revocation.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody;
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
    public HttpEntity<RevocationInfo> get(final Long from) {
        Collection<StoredRevocation> revocations = storage.getRevocations(from);

        List<Revocation> apiRevocations = new ArrayList<>(revocations.size());
        for (StoredRevocation stored : revocations) {
            final RevocationData data = stored.getData();

            Revocation newRevocation = new Revocation();
            newRevocation.setRevokedAt(stored.getRevokedAt());
            newRevocation.setType(stored.getType());

            if (data instanceof StoredGlobal) {
                GlobalRevocation apiData = new GlobalRevocation();
                apiData.setIssuedBefore(((StoredGlobal) data).getIssued_before());
                newRevocation.setData(apiData);
            } else if (data instanceof StoredClaim) {

                // here hash from stored value to hashed value sha-2
                throw new RuntimeException("NOT_IMPLEMENTED_YET");
            } else if (data instanceof StoredToken) {
                TokenRevocation apiData = new TokenRevocation();
                apiData.setTokenHash(((StoredToken) data).getTokenHash());
                newRevocation.setData(apiData);
            }

            apiRevocations.add(newRevocation);
        }


        RevocationInfo responseBody = new RevocationInfo();
        responseBody.setRevocations(apiRevocations);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @Override
    public HttpEntity<String> post(@RequestBody final Revocation r) {
        RevocationData data = null;
        switch (r.getType()) {
            case CLAIM :
                ClaimRevocation cr = (ClaimRevocation) r.getData();

                // hash value is not hashed on the way in
                data = new StoredClaim(cr.getName(), cr.getValueHash(), cr.getIssuedBefore());
                break;

            case TOKEN :
                TokenRevocation tr = (TokenRevocation) r.getData();
                data = new StoredToken(tr.getTokenHash());
                break;

            case GLOBAL :
                GlobalRevocation gr = (GlobalRevocation) r.getData();
                data = new StoredGlobal(gr.getIssuedBefore());
                break;
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
