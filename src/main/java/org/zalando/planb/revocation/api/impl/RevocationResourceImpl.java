package org.zalando.planb.revocation.api.impl;

import java.util.*;

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
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.domain.ClaimRevocationData;
import org.zalando.planb.revocation.domain.GlobalRevocationData;
import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.domain.Revocation;
import org.zalando.planb.revocation.domain.NotificationType;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.TokenRevocationData;
import org.zalando.planb.revocation.persistence.CassandraStore;
import org.zalando.planb.revocation.persistence.RevocationData;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.persistence.StoredClaim;
import org.zalando.planb.revocation.persistence.StoredGlobal;
import org.zalando.planb.revocation.persistence.StoredRevocation;
import org.zalando.planb.revocation.persistence.StoredToken;
import org.zalando.planb.revocation.util.MessageHasher;
import org.zalando.planb.revocation.util.UnixTimestamp;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@RestController
@RequestMapping(value = "/revocations", produces = MediaType.APPLICATION_JSON_VALUE)
public class RevocationResourceImpl implements RevocationResource {

    @Autowired
    private RevocationStore storage;

    @Autowired
    private MessageHasher messageHasher;

    @Autowired
    private CassandraProperties cassandraProperties;

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RevocationInfo get(@RequestParam(required = true) final int from) {
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
        responseBody.setMeta(metaInformation());
        responseBody.setRevocations(apiRevocations);

        return responseBody;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public HttpEntity<String> post(@RequestBody final Revocation revocation) {
        RevocationData data = null;
        switch (revocation.getType()) {

            case CLAIM :

                ClaimRevocationData cr = (ClaimRevocationData) revocation.getData();

                data = new StoredClaim(cr.getName(), cr.getValueHash(), Optional.ofNullable(cr.getIssuedBefore()).orElse(UnixTimestamp.now()));
                break;

            case TOKEN :

                TokenRevocationData tr = (TokenRevocationData) revocation.getData();
                data = new StoredToken(tr.getTokenHash());
                break;

            case GLOBAL :

                GlobalRevocationData gr = (GlobalRevocationData) revocation.getData();
                data = new StoredGlobal(gr.getIssuedBefore());
                break;
        }

        if (data == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        StoredRevocation storedRevocation = new StoredRevocation(data, revocation.getType(), "<!--add revoked by-->");
        if (storage.storeRevocation(storedRevocation)) {

            // TODO Refactor
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private EnumMap<NotificationType, Object> metaInformation() {
        EnumMap<NotificationType, Object> metaInfo = new EnumMap<>(NotificationType.class);

        if(storage instanceof CassandraStore) {
            metaInfo.put(NotificationType.MAX_TIME_DELTA, cassandraProperties.getMaxTimeDelta());
        }

        Refresh refresh = storage.getRefresh();

        if(refresh != null) {
            metaInfo.put(NotificationType.REFRESH_FROM, refresh.refreshFrom());
            metaInfo.put(NotificationType.REFRESH_TIMESTAMP, refresh.refreshTimestamp());
        }

        return metaInfo;
    }
}
