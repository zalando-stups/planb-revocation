package org.zalando.planb.revocation.api.impl;

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
import org.zalando.planb.revocation.domain.NotificationType;
import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationList;
import org.zalando.planb.revocation.domain.RevocationType;
import org.zalando.planb.revocation.domain.RevokedClaimsData;
import org.zalando.planb.revocation.domain.RevokedClaimsInfo;
import org.zalando.planb.revocation.domain.RevokedData;
import org.zalando.planb.revocation.domain.RevokedGlobal;
import org.zalando.planb.revocation.domain.RevokedInfo;
import org.zalando.planb.revocation.domain.RevokedTokenData;
import org.zalando.planb.revocation.domain.RevokedTokenInfo;
import org.zalando.planb.revocation.persistence.CassandraStore;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.util.MessageHasher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

/**
 * TODO: small javadoc
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
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
    public RevocationList get(@RequestParam(required = true) final int from) {
        Collection<RevocationData> revocations = storage.getRevocations(from);

        List<RevocationInfo> apiRevocations = new ArrayList<>(revocations.size());
        for (RevocationData stored : revocations) {
            final RevokedData data = stored.getData();

            RevocationInfo newRevocation = new RevocationInfo();
            newRevocation.setRevokedAt(stored.getRevokedAt());
            newRevocation.setType(stored.getType());

            if (data instanceof RevokedGlobal) {
                // No transformation necessary
                newRevocation.setData((RevokedInfo) data);

            } else if (data instanceof RevokedClaimsData) {
                RevokedClaimsInfo revokedClaims = new RevokedClaimsInfo();

                revokedClaims.setNames(((RevokedClaimsData) data).getClaims().keySet());
                revokedClaims.setValueHash(messageHasher.hashAndEncode(RevocationType.CLAIM,
                        ((RevokedClaimsData) data).getClaims().values()));
                revokedClaims.setHashAlgorithm(messageHasher.getHashers().get(RevocationType.CLAIM).getAlgorithm());
                revokedClaims.setIssuedBefore(((RevokedClaimsData) data).getIssuedBefore());
                revokedClaims.setSeparator(messageHasher.getSeparator());

                newRevocation.setData(revokedClaims);

            } else if (data instanceof RevokedTokenData) {
                RevokedTokenInfo revokedToken = new RevokedTokenInfo();
                revokedToken.setTokenHash(messageHasher.hashAndEncode(RevocationType.TOKEN,
                        ((RevokedTokenData) data).getToken()));
                revokedToken.setHashAlgorithm(messageHasher.getHashers().get(RevocationType.TOKEN).getAlgorithm());
                newRevocation.setData(revokedToken);
            }

            apiRevocations.add(newRevocation);
        }

        RevocationList responseBody = new RevocationList();
        responseBody.setMeta(metaInformation());
        responseBody.setRevocations(apiRevocations);

        return responseBody;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public HttpEntity<String> post(@RequestBody final RevocationData revocation) {

        if (storage.storeRevocation(revocation)) {

            // TODO Refactor
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private EnumMap<NotificationType, Object> metaInformation() {
        EnumMap<NotificationType, Object> metaInfo = new EnumMap<>(NotificationType.class);

        if (storage instanceof CassandraStore) {
            metaInfo.put(NotificationType.MAX_TIME_DELTA, cassandraProperties.getMaxTimeDelta());
        }

        Refresh refresh = storage.getRefresh();

        if (refresh != null) {
            metaInfo.put(NotificationType.REFRESH_FROM, refresh.refreshFrom());
            metaInfo.put(NotificationType.REFRESH_TIMESTAMP, refresh.refreshTimestamp());
        }

        return metaInfo;
    }
}
