package org.zalando.planb.revocation.api.impl;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

import org.zalando.planb.revocation.api.RevocationResource;
import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.persistence.TokenRevocation;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@RestController
public class RevocationResourceImpl implements RevocationResource {

    private final RevocationStore store;

    public RevocationResourceImpl(RevocationStore store) {
        this.store=store;
    }

    @Override
    public HttpEntity<RevocationInfo> get() {
        store.getRevocations();
    }

    @Override
    public HttpEntity<String> post(final RevocationInfo rev) {
        store.storeRevocation(new TokenRevocation(""));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
