package org.zalando.planb.revocation.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.EnumMap;

import org.zalando.planb.revocation.domain.RevocationType;

import lombok.Value;

/**
 * Created by rreis on 2/18/16.
 */
@Value
public class MessageHasher {

    private final EnumMap<RevocationType, MessageDigest> hashers;

    private final String salt;

    public MessageHasher(final EnumMap<RevocationType, String> hashingAlgorithms, final String salt)
        throws NoSuchAlgorithmException {
        hashers = new EnumMap<>(RevocationType.class);
        this.salt = (salt == null) ? "" : salt;

        if (hashingAlgorithms == null) {
            return;
        }

        for (RevocationType type : hashingAlgorithms.keySet()) {
            hashers.put(type, MessageDigest.getInstance(hashingAlgorithms.get(type)));
        }
    }

    public String hash(final RevocationType type, final String message) {
        String hashed = message;

        if (hashers.containsKey(type)) {
            hashers.get(type).update((salt + message).getBytes());
            hashed = new String(hashers.get(type).digest());
        }

        return hashed;
    }
}
