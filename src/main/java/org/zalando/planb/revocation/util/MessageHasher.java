package org.zalando.planb.revocation.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by rreis on 2/18/16.
 */
public class MessageHasher {

    private final MessageDigest messageDigest;

    private final String salt;

    public MessageHasher(String algorithm, String salt) throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance(algorithm);
        this.salt = salt;
    }

    public String hash(String message) {

        String saltMessage = salt + message;

        messageDigest.update(saltMessage.getBytes());

        return new String(messageDigest.digest());
    }
}
