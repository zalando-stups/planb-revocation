package org.zalando.planb.revocation.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.EnumMap;

import org.zalando.planb.revocation.domain.RevocationType;

/**
 * Created by rreis on 2/18/16.
 */
public class MessageHasherUtil {

    private static EnumMap<RevocationType, MessageDigest> hashers = new EnumMap<>(RevocationType.class);

    private static String salt;

    public static String hash(final RevocationType type, final String message) {

        String hashed = message;
        if (hashers.containsKey(type)) {
            hashers.get(type).update(((salt == null ? "" : salt) + message).getBytes());
            hashed = new String(hashers.get(type).digest());
        }

        return hashed;
    }

    public static void setHashers(final EnumMap<RevocationType, String> hashingAlgorithms)
        throws NoSuchAlgorithmException {

        for (RevocationType type : hashingAlgorithms.keySet()) {
            hashers.put(type, MessageDigest.getInstance(hashingAlgorithms.get(type)));
        }
    }

    public static void setSalt(final String salt) {
        MessageHasherUtil.salt = salt;
    }
}
