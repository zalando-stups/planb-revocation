package org.zalando.planb.revocation.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Base64;
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

    /**
     * Hashes the specified message using the algorithm specified by the <code>RevocationType</code> parameter. Returns
     * a Base64 URL encoding of the Hash.
     *
     * @param   type     algorithm to use
     * @param   message  the message to hash.
     *
     * @return  a Base64 URL encoded version of the hash.
     */
    public String hashAndEncode(final RevocationType type, final String message) {
        byte[] hashed = message.getBytes();

        if (hashers.containsKey(type)) {
            hashers.get(type).update((salt + message).getBytes());
            hashed = hashers.get(type).digest();
        }

        return Base64.getUrlEncoder().encodeToString(hashed);
    }
}
