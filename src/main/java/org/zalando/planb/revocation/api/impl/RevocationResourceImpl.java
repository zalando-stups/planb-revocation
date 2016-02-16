package org.zalando.planb.revocation.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

import org.zalando.planb.revocation.api.RevocationResource;
import org.zalando.planb.revocation.domain.*;
import org.zalando.planb.revocation.persistence.*;
import org.zalando.planb.revocation.persistence.RevocationData;
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
    public HttpEntity<RevocationInfo> get(Long from) {
        Collection<StoredRevocation> revocations = storage.getRevocations(from);

        List<Revocation> apiRevocations = new ArrayList<>(revocations.size());
        for(StoredRevocation stored : revocations) {
            final RevocationData data = stored.getData();

            org.zalando.planb.revocation.domain.RevocationData apiData = null;
            if(data instanceof StoredGlobal) {
                apiData = GlobalRevocation.builder().issuedBefore(((StoredGlobal) data).getIssued_before()).build();
            }
            else if(data instanceof StoredClaim) {
                // here hash from stored value to hashed value sha-2
                throw new NotImplementedException();
            }
            else if(data instanceof StoredToken) {
                apiData = TokenRevocation.builder().tokenHash(((StoredToken) data).getTokenHash()).build();
            }

            Revocation revocation = Revocation.builder().data(apiData).revokedAt(stored.getRevokedAt()).type(stored.getType()).build();
            apiRevocations.add(revocation);
        }

        RevocationInfo wrapper = RevocationInfo.builder().revocations(apiRevocations).build();
//        return new ResponseEntity<>(wrapper, HttpStatus.OK);
        return new ResponseEntity<>(RevocationInfo.builder().revocations(
                Arrays.asList(
                        Revocation.builder().type(RevocationType.CLAIM).revokedAt(System.currentTimeMillis()).data(
                                ClaimRevocation.builder().name("sub").valueHash("asdpoifh").issuedBefore(
                                        System.currentTimeMillis()).build()).build())).build(), HttpStatus.OK);
    }

    @Override
    public HttpEntity<String> post(Revocation r) {
        RevocationData data = null;
        switch(r.getType()) {
            case CLAIM: {
                ClaimRevocation cr = (ClaimRevocation) r.getData();
                // hash value is not hashed on the way in
                data = new StoredClaim(cr.getName(), cr.getValueHash(), cr.getIssuedBefore());
                break;
            }
            case TOKEN: {
                TokenRevocation tr = (TokenRevocation) r.getData();
                data = new StoredToken(tr.getTokenHash());
                break;
            }
            case GLOBAL: {
                GlobalRevocation gr = (GlobalRevocation) r.getData();
                data = new StoredGlobal(gr.getIssuedBefore());
                break;
            }
        }

        if (null == data) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        StoredRevocation storedRevocation = new StoredRevocation(data, r.getType(), "<!--add revoked by-->");
        if(storage.storeRevocation(storedRevocation)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
