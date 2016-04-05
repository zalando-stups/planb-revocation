package org.zalando.planb.revocation.persistence;

import org.zalando.planb.revocation.domain.AuthorizationRule;

import java.util.Collection;

public interface AuthorizationRulesStore {

    Collection<AuthorizationRule> getAccessList(AuthorizationRule authorizationRule);

    boolean storeAccessRule(AuthorizationRule authorizationRule);

}
