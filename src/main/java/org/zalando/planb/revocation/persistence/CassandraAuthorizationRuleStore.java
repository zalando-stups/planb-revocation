package org.zalando.planb.revocation.persistence;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.zalando.planb.revocation.domain.AuthorizationRule;
import org.zalando.planb.revocation.domain.ImmutableAuthorizationRule;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.driver.core.querybuilder.QueryBuilder.now;

@Slf4j
public class CassandraAuthorizationRuleStore implements AuthorizationRulesStore.Internal {

    private static final long RELOAD_IN_MS =  1000 * 60 * 10; // 10 minutes

    private List<AuthorizationRule> inMemoryRuleStore = Collections.emptyList();

    private final Session session;

    private final PreparedStatement getRules;

    private final PreparedStatement insertRule;

    private static final String AUTHORIZATION_TABLE = "authorization_rule";
    private static final String REQUIRED_USER_CLAIMS = "required_user_claims";
    private static final String ALLOWED_REVOCATION_CLAIMS = "allowed_revocation_claims";
    private static final String CREATED_BY = "created_by";
    private static final String LAST_MODIFIED_BY = "last_modified_by";
    private static final String UUID = "uuid";

    private static final RegularStatement INSERT_AUTHORIZATION = QueryBuilder.insertInto(AUTHORIZATION_TABLE)
            .value(UUID, now())
            .value(REQUIRED_USER_CLAIMS, bindMarker())
            .value(ALLOWED_REVOCATION_CLAIMS, bindMarker())
            .value(CREATED_BY, bindMarker())
            .value(LAST_MODIFIED_BY, bindMarker());
    private static final RegularStatement SELECT_AUTHORIZATION = QueryBuilder.select()
            .column(REQUIRED_USER_CLAIMS)
            .column(ALLOWED_REVOCATION_CLAIMS)
            .from(AUTHORIZATION_TABLE);
    private static final RegularStatement CLEANUP_AUTHORIZATION = QueryBuilder.truncate(AUTHORIZATION_TABLE);

    public CassandraAuthorizationRuleStore(final Session session, final ConsistencyLevel read, final ConsistencyLevel write) {
        this.session = session;
        getRules = session.prepare(SELECT_AUTHORIZATION).setConsistencyLevel(read);
        insertRule = session.prepare(INSERT_AUTHORIZATION).setConsistencyLevel(write);
    }

    @PostConstruct
    void initializeAuthorizationRuleStore() throws Exception {
        loadAuthorizationRuleStore();
        log.debug("Authorization rule store initialized");
    }

    @Scheduled(fixedDelay = RELOAD_IN_MS, initialDelay = RELOAD_IN_MS)
    void loadAuthorizationRuleStore() {
        inMemoryRuleStore = Optional.ofNullable(getRules.bind())
                .map(session::execute)
                .map(ResultSet::all)
                .map(this::toAuthorizationRules)
                .orElse(Collections.emptyList());
    }

    @Override
    public Collection<AuthorizationRule> retrieveByMatchingAllowedClaims(AuthorizationRule authorizationRule) {
        return findMatchingRulesByAllowedClaims(inMemoryRuleStore, authorizationRule);
    }

    @Override
    public void store(AuthorizationRule authorizationRule) {
        final BoundStatement insert = insertRule.bind()
                                    .setMap(REQUIRED_USER_CLAIMS, authorizationRule.requiredUserClaims())
                                    .setMap(ALLOWED_REVOCATION_CLAIMS, authorizationRule.allowedRevocationClaims())
                                    .setString(CREATED_BY, null)
                                    .setString(LAST_MODIFIED_BY, null);
        session.execute(insert);
        loadAuthorizationRuleStore();
    }

    private List<AuthorizationRule> toAuthorizationRules(List<Row> rows) {
        return rows.stream().map(this::toAuthorizationRule).collect(Collectors.toList());
    }

    private AuthorizationRule toAuthorizationRule(Row row) {
        return ImmutableAuthorizationRule.builder()
                    .requiredUserClaims(row.getMap(REQUIRED_USER_CLAIMS, String.class, String.class))
                    .allowedRevocationClaims(row.getMap(ALLOWED_REVOCATION_CLAIMS, String.class, String.class))
                    .build();
    }


    @Override
    public void cleanup() {
        inMemoryRuleStore = Collections.emptyList();
        session.execute(CLEANUP_AUTHORIZATION);
    }
}
