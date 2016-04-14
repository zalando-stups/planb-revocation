package org.zalando.planb.revocation.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Base64;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.SpringApplicationConfiguration;

import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;

import org.zalando.planb.revocation.AbstractSpringTest;
import org.zalando.planb.revocation.Main;
import org.zalando.planb.revocation.config.properties.HashingProperties;
import org.zalando.planb.revocation.domain.RevocationType;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("test")
public class MessageHashTest extends AbstractSpringTest {

    private static final String MESSAGE = "A very secret Message";
    private static final String MESSAGE2 = "Another very secret Message";

    @Autowired
    MessageHasher messageHasher;

    @Autowired
    HashingProperties hashingProperties;

    /**
     * Asserts that a {@link MessageHasher} properly hashes and encodes a MESSAGE.
     *
     * @throws  NoSuchAlgorithmException
     */
    @Test
    public void testMessageHashing() throws NoSuchAlgorithmException {
        MessageDigest hasher = MessageDigest.getInstance(hashingProperties.getAlgorithms().get(RevocationType.TOKEN));
        hasher.update((hashingProperties.getSalt() + MESSAGE).getBytes());

        String expected = Base64.getUrlEncoder().encodeToString(hasher.digest());

        String base64sha256Hashed = messageHasher.hashAndEncode(RevocationType.TOKEN, MESSAGE);

        assertEquals(expected, base64sha256Hashed);
    }

    /**
     * Asserts that a {@link MessageHasher} properly hashes and encodes concatenated a MESSAGE.
     *
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testConcatenatedHashing() throws NoSuchAlgorithmException {
        String concatenatedMessage = MESSAGE + hashingProperties.getSeparator() + MESSAGE2;
        MessageDigest hasher = MessageDigest.getInstance(hashingProperties.getAlgorithms().get(RevocationType.CLAIM));
        hasher.update((hashingProperties.getSalt() + concatenatedMessage).getBytes());

        String expected = Base64.getUrlEncoder().encodeToString(hasher.digest());
        String base64sha256Hashed = messageHasher.hashAndEncode(RevocationType.CLAIM, MESSAGE, MESSAGE2);


        assertEquals(expected, base64sha256Hashed);
    }

    /**
     * Asserts that, when no encryption algorithm is set for a {@link MessageHasher}, only the Base64 encding is
     * performed.
     *
     * @throws  NoSuchAlgorithmException
     */
    @Test
    public void testNullHashing() throws NoSuchAlgorithmException {
        MessageHasher nullHasher = ImmutableMessageHasher.builder().separator('|').build();

        assertEquals(Base64.getEncoder().encodeToString(MESSAGE.getBytes()),
            nullHasher.hashAndEncode(RevocationType.TOKEN, MESSAGE));
    }

    /**
     * Tests default value properties for the MessageHasher.
     */
    @Test
    public void testDefaultValues() {
        assertEquals("SHA-256", messageHasher.hashingAlgorithms().get(RevocationType.TOKEN).getAlgorithm());
        assertEquals("SHA-256", messageHasher.hashingAlgorithms().get(RevocationType.CLAIM).getAlgorithm());
        assertEquals('|', (char)messageHasher.separator());
    }
}