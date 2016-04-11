package org.zalando.planb.revocation.persistence;

import org.zalando.planb.revocation.domain.AuthorizationRule;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Provides persistence for {@link AuthorizationRule} that will be used
 * to authorize revocations.
 */
public interface AuthorizationRulesStore {

    /**
     * Determines which persisted {@link AuthorizationRule rules} authorize the argument request.
     * <p />
     * This is done by retrieving all persisted {@link AuthorizationRule rules}
     * whose allowed revocation claims are contained in the argument's allowed revocation claims.
     *
     * @param authorizationRule the revocation request we will be
     *                          searching for authorizing {@link AuthorizationRule rules} in the store
     * @return found authorizing {@link AuthorizationRule rules}. May be empty.
     */
    Collection<AuthorizationRule> retrieveByMatchingAllowedClaims(AuthorizationRule authorizationRule);

    /**
     * Persists the argument {@link AuthorizationRule rule} to the store.
     * Can be retrieved via {@link AuthorizationRulesStore#retrieveByMatchingAllowedClaims(AuthorizationRule)}
     *
     * @param authorizationRule The {@link AuthorizationRule rules} to persist.
     */
    void store(AuthorizationRule authorizationRule);

    /**
     * Given a collection of candidate {@link AuthorizationRule rules}, determines all matching by
     * a reference rule's {@link AuthorizationRule#allowedRevocationClaims()}.
     *
     * @param candidateMatchingRules the candidate {@link AuthorizationRule rules} to look for matches
     * @param referenceRule the reference {@link AuthorizationRule rule} to look for matches
     * @return all matching {@link AuthorizationRule rules} whose {@link AuthorizationRule#allowedRevocationClaims()}
     *          match the reference's.
     */
    default Collection<AuthorizationRule> findMatchingRulesByAllowedClaims(Collection<AuthorizationRule>
                                                                                   candidateMatchingRules,
                                                                           AuthorizationRule referenceRule) {
        return candidateMatchingRules.stream()
                .filter(referenceRule::matchesAllowedRevocationClaims)
                .collect(Collectors.toSet());
    }

    /**
     * Only intended for internal use, clients should not implement this
     */
    interface Internal extends AuthorizationRulesStore {

        /**
         * Removes all stored {@link AuthorizationRule rules}
         */
        void cleanup();

    }
}
