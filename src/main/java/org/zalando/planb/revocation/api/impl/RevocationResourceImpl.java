package org.zalando.planb.revocation.api.impl;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.planb.revocation.api.RevocationResource;
import org.zalando.planb.revocation.api.exception.FutureRevocationException;
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.config.properties.RevocationProperties;
import org.zalando.planb.revocation.domain.NotificationType;
import org.zalando.planb.revocation.domain.Refresh;
import org.zalando.planb.revocation.domain.RevocationData;
import org.zalando.planb.revocation.domain.RevocationInfo;
import org.zalando.planb.revocation.domain.RevocationList;
import org.zalando.planb.revocation.domain.RevocationRequest;
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
import org.zalando.planb.revocation.service.RevocationAuthorizationService;
import org.zalando.planb.revocation.util.MessageHasher;
import org.zalando.planb.revocation.util.UnixTimestamp;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import static java.time.Instant.ofEpochSecond;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Controller implementation for the revocations endpoint.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@RestController
@RequestMapping(value = "/revocations", produces = MediaType.APPLICATION_JSON_VALUE)
public class RevocationResourceImpl implements RevocationResource {

    private final Logger log = getLogger(getClass());

    @Autowired
    private RevocationStore storage;

    @Autowired
    private MessageHasher messageHasher;

    @Autowired
    private CassandraProperties cassandraProperties;

    @Autowired
    private RevocationAuthorizationService revocationAuthorizationService;

    @Autowired
    private RevocationProperties revocationProperties;

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RevocationList get(@RequestParam final int from) {
        log.debug("GET revocations since {} ({})", from, ZonedDateTime.ofInstant(ofEpochSecond(from), ZoneId.systemDefault()));
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

    /**
     * Posts the specified revocation to be stored.
     * <p>
     * <p>Revokes tokens associated with the specified revocation type.</p>
     * <p>
     * <p>If the field {@code issued_before} is a timestamp set in the future, returns {@link HttpStatus#BAD_REQUEST}.
     * </p>
     *
     * @param revocation the revocation associated with the tokens to revoke
     */
    @Override
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void post(@RequestBody final RevocationRequest revocation) {

        revocationAuthorizationService.checkAuthorization(revocation);

        Integer timestamp = null;
        switch (revocation.getType()) {
            case TOKEN:
                timestamp = ((RevokedTokenData) revocation.getData()).getIssuedBefore();
                break;
            case CLAIM:
                timestamp = ((RevokedClaimsData) revocation.getData()).getIssuedBefore();
                break;
            case GLOBAL:
                timestamp = ((RevokedGlobal) revocation.getData()).getIssuedBefore();
                break;
        }

        // Checks for future timestamps
        if (timestamp > UnixTimestamp.now() + revocationProperties.getTimestampThreshold()) {
            throw new FutureRevocationException();
        }

        storage.storeRevocation(revocation);
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
