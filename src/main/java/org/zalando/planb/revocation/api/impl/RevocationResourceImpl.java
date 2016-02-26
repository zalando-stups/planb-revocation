package org.zalando.planb.revocation.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.zalando.planb.revocation.api.RevocationResource;
import org.zalando.planb.revocation.domain.ClaimRevocationData;
import org.zalando.planb.revocation.domain.GlobalRevocationData;
import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.TokenRevocationData;
import org.zalando.planb.revocation.persistence.RevocationData;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.persistence.StoredClaim;
import org.zalando.planb.revocation.persistence.StoredGlobal;
import org.zalando.planb.revocation.persistence.StoredRevocation;
import org.zalando.planb.revocation.persistence.StoredToken;
import org.zalando.planb.revocation.util.MessageHasher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@RestController
@RequestMapping(value = "/revocations", produces = MediaType.APPLICATION_JSON_VALUE)
public class RevocationResourceImpl implements RevocationResource {

    @Autowired
    RevocationStore storage;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageHasher messageHasher;

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String get(@RequestParam final Long from) throws JsonProcessingException {
        Collection<StoredRevocation> revocations = storage.getRevocations(from);

        List<Revocation> apiRevocations = new ArrayList<>(revocations.size());
        for (StoredRevocation stored : revocations) {
            final RevocationData data = stored.getData();

            Revocation newRevocation = new Revocation();
            newRevocation.setRevokedAt(stored.getRevokedAt());
            newRevocation.setType(stored.getType());

            if (data instanceof StoredGlobal) {
                GlobalRevocationData apiData = new GlobalRevocationData();
                apiData.setIssuedBefore(((StoredGlobal) data).getIssued_before());
                newRevocation.setData(apiData);

            } else if (data instanceof StoredClaim) {
                ClaimRevocationData apiData = new ClaimRevocationData();
                apiData.setName(((StoredClaim) data).getClaimName());
                apiData.setValueHash(messageHasher.hashAndEncode(RevocationType.CLAIM,
                        ((StoredClaim) data).getClaimValue()));
                apiData.setHashAlgorithm(messageHasher.getHashers().get(RevocationType.CLAIM).getAlgorithm());
                apiData.setIssuedBefore(((StoredClaim) data).getIssuedBefore());
                newRevocation.setData(apiData);

            } else if (data instanceof StoredToken) {
                TokenRevocationData apiData = new TokenRevocationData();
                apiData.setTokenHash(messageHasher.hashAndEncode(RevocationType.TOKEN,
                        ((StoredToken) data).getTokenHash()));
                apiData.setHashAlgorithm(messageHasher.getHashers().get(RevocationType.TOKEN).getAlgorithm());
                newRevocation.setData(apiData);
            }

            apiRevocations.add(newRevocation);
        }

        RevocationInfo responseBody = new RevocationInfo();
        responseBody.setRevocations(apiRevocations);

        return objectMapper.writeValueAsString(responseBody);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public HttpEntity<String> post(@RequestBody final Revocation r) {
        RevocationData data = null;
        switch (r.getType()) {

            case CLAIM :

                ClaimRevocationData cr = (ClaimRevocationData) r.getData();

                data = new StoredClaim(cr.getName(), cr.getValueHash(), cr.getIssuedBefore());
                break;

            case TOKEN :

                TokenRevocationData tr = (TokenRevocationData) r.getData();
                data = new StoredToken(tr.getTokenHash());
                break;

            case GLOBAL :

                GlobalRevocationData gr = (GlobalRevocationData) r.getData();
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
