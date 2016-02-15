package org.zalando.planb.revocation.api.impl;

import java.time.Instant;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

import org.zalando.planb.revocation.api.RevocationResource;
import org.zalando.planb.revocation.domain.ClaimRevocation;
import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationType;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@RestController
public class RevocationResourceImpl implements RevocationResource {

    @Override
    public HttpEntity<RevocationInfo> get() {

        // TODO implement logic here
        return new ResponseEntity<>(RevocationInfo.builder().revocations(
                    Arrays.asList(
                        Revocation.builder().type(RevocationType.CLAIM).revokedAt(Instant.now()).data(
                            ClaimRevocation.builder().name("sub").valueHash("asdpoifh").issuedBefore(Instant.now())
                                    .build()).build())).build(), HttpStatus.OK);
    }

    @Override
    public HttpEntity<String> post(final RevocationInfo rev) {

        // TODO implement logic here
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
