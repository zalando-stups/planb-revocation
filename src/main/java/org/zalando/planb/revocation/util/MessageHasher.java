package org.zalando.planb.revocation.util;

/**
 * Created by rreis on 2/18/16.
 */
public class MessageHasher {

    private final String salt;

    // TODO after the test :)
    public MessageHasher(String algorithm, String salt) {
        this.salt = salt;
    }

    public String hash(String message) {
        return null;
    }
}
