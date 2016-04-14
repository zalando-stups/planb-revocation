package org.zalando.planb.revocation.util;

import org.immutables.value.Value;
import org.zalando.planb.revocation.domain.RevocationType;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;

/**
 * Utility class to hash messages.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
@Value.Immutable
public abstract class MessageHasher {

    /**
     * Returns a map containing hashers for each revocation type.
     * <p>
     * <p>Defaults to an empty map, meaning no hashing is done.</p>
     *
     * @return the aforementioned map
     */
    public abstract Map<RevocationType, MessageDigest> hashingAlgorithms();

    /**
     * Returns the salt used to hash messages.
     * <p>
     * <p>The same salt must be used by other parties comparing the same hashed message.</p>
     *
     * @return the hashing salt
     */
    @Value.Default
    public String salt() {
        return "";
    }

    /**
     * Returns the separator used to hash multiple messages.
     *
     * @return the separator
     */
    public abstract Character separator();

    /**
     * Hashes the specified messages using the algorithm specified by the <code>RevocationType</code> parameter. Returns
     * a Base64 URL encoding of the Hash.
     * <p>
     * <p>If there are multiples messages, they are concatenated using the provided separator, prior to hashing.</p>
     *
     * @param type     algorithm to use
     * @param messages the messages to hash.
     * @return a Base64 URL encoded version of the hash.
     */
    public String hashAndEncode(final RevocationType type, final String... messages) {

        StringBuilder messageConcatenated = new StringBuilder();
        for (String message : messages) {
            messageConcatenated.append(message);
            messageConcatenated.append(separator());
        }

        String message = messageConcatenated.substring(0, messageConcatenated.length() - 1);

        byte[] hashed = message.getBytes();

        if (hashingAlgorithms().containsKey(type)) {

            hashingAlgorithms().get(type).update((salt() + message).getBytes());
            hashed = hashingAlgorithms().get(type).digest();
        }

        return Base64.getUrlEncoder().encodeToString(hashed);
    }

    /**
     * Hashes the specified messages using the algorithm specified by the <code>RevocationType</code> parameter. Returns
     * a Base64 URL encoding of the Hash.
     * <p>
     * <p>If there are multiples messages, they are concatenated using the provided separator, prior to hashing.</p>
     *
     * @param type     algorithm to use
     * @param messages the messages to hash.
     * @return a Base64 URL encoded version of the hash.
     */
    public String hashAndEncode(final RevocationType type, final Collection<String> messages) {
        return hashAndEncode(type, messages.toArray(new String[]{}));
    }
}
