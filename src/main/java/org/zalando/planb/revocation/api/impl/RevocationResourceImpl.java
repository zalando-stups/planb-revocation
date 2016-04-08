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
import org.zalando.planb.revocation.config.properties.CassandraProperties;
import org.zalando.planb.revocation.domain.ImmutableRevokedClaimsInfo;
import org.zalando.planb.revocation.domain.ImmutableRevokedTokenInfo;
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
import org.zalando.planb.revocation.persistence.CassandraRevocationStore;
import org.zalando.planb.revocation.persistence.RevocationStore;
import org.zalando.planb.revocation.service.RevocationAuthorizationService;
import org.zalando.planb.revocation.util.MessageHasher;

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

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RevocationList get(@RequestParam final int from) {
        log.debug("GET revocations since {} ({})", from, ZonedDateTime.ofInstant(ofEpochSecond(from), ZoneId.systemDefault()));
        Collection<RevocationData> revocations = storage.getRevocations(from);

        List<RevocationInfo> apiRevocations = new ArrayList<>(revocations.size());
        for (RevocationData stored : revocations) {
            final RevokedData data = stored.revocationRequest().data();

            RevokedInfo revokedInfo = null;
            if (data instanceof RevokedGlobal) {
                // No transformation necessary
                revokedInfo = (RevokedInfo) data;

            } else if (data instanceof RevokedClaimsData) {
                revokedInfo = ImmutableRevokedClaimsInfo.builder()
                        .names(((RevokedClaimsData) data).claims().keySet())
                        .valueHash(messageHasher.hashAndEncode(RevocationType.CLAIM,
                                ((RevokedClaimsData) data).claims().values()))
                        .hashAlgorithm(messageHasher.getHashers().get(RevocationType.CLAIM).getAlgorithm())
                        .issuedBefore(((RevokedClaimsData) data).issuedBefore())
                        .separator(messageHasher.getSeparator())
                        .build();

            } else if (data instanceof RevokedTokenData) {
                revokedInfo = ImmutableRevokedTokenInfo.builder()
                        .tokenHash(messageHasher.hashAndEncode(RevocationType.TOKEN, ((RevokedTokenData) data).token()))
                        .hashAlgorithm(messageHasher.getHashers().get(RevocationType.TOKEN).getAlgorithm())
                        .issuedBefore(((RevokedTokenData) data).issuedBefore())
                        .build();
            }

            apiRevocations.add(ImmutableRevocationInfo.builder()
                    .type(stored.revocationRequest().type())
                    .revokedAt(stored.revokedAt())
                    .data(revokedInfo)
                    .build());
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
        storage.storeRevocation(revocation);
    }

    private EnumMap<NotificationType, Object> metaInformation() {
        EnumMap<NotificationType, Object> metaInfo = new EnumMap<>(NotificationType.class);

        if (storage instanceof CassandraRevocationStore) {
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
