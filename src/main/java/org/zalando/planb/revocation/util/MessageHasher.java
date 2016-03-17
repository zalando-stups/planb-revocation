package org.zalando.planb.revocation.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Base64;
import java.util.EnumMap;

import org.zalando.planb.revocation.domain.RevocationType;

import lombok.Value;

/**
 * Message hasher.
 *
 * @author  <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value
public class MessageHasher {

    private final EnumMap<RevocationType, MessageDigest> hashers;

    private final String salt;

    private final Character separator;

    public MessageHasher(final EnumMap<RevocationType, String> hashingAlgorithms, final String salt, final Character
            separator)
        throws NoSuchAlgorithmException {
        hashers = new EnumMap<>(RevocationType.class);
        this.salt = (salt == null) ? "" : salt;
        this.separator = separator;

        if (hashingAlgorithms == null) {
            return;
        }

        for (RevocationType type : hashingAlgorithms.keySet()) {
            hashers.put(type, MessageDigest.getInstance(hashingAlgorithms.get(type)));
        }
    }

    /**
     * Hashes the specified messages using the algorithm specified by the <code>RevocationType</code> parameter. Returns
     * a Base64 URL encoding of the Hash.
     *
     * <p>If there are multiples messages, they are concatenated using the provided separator, prior to hashing.</p>
     *
     * @param   type     algorithm to use
     * @param   messages  the messages to hash.
     *
     * @return  a Base64 URL encoded version of the hash.
     */
    public String hashAndEncode(final RevocationType type, final String... messages) {
        StringBuilder messageConcatenated = new StringBuilder();
        for(String message : messages) {
            messageConcatenated.append(message + (separator != null ? separator : ""));
        }

        String message = separator == null ? messageConcatenated.toString() : messageConcatenated.substring(0,
                messageConcatenated.length() - 1);

        byte[] hashed = message.getBytes();

        if (hashers.containsKey(type)) {
            hashers.get(type).update((salt + message).getBytes());
            hashed = hashers.get(type).digest();
        }

        return Base64.getUrlEncoder().encodeToString(hashed);
    }
}
