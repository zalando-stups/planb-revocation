package org.zalando.planb.revocation.util;

import static org.junit.Assert.assertEquals;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.SpringApplicationConfiguration;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.zalando.planb.revocation.config.HashingConfig;
import org.zalando.planb.revocation.config.properties.HashingProperties;
import org.zalando.planb.revocation.domain.RevocationType;

/**
 * TODO: small javadoc
 *
 * @author  <a href="mailto:team-greendale@zalando.de">Team Greendale</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HashingConfig.class)
public class MessageHashTest {

    private static final String message = "aVerySecretMessage";

    @Autowired
    MessageHasher messageHasher;

    @Autowired
    HashingProperties hashingProperties;

    @Test
    public void testSHA256Hashing() throws NoSuchAlgorithmException {
        MessageDigest hasher = MessageDigest.getInstance("SHA-256");
        hasher.update((hashingProperties.getSalt() + message).getBytes());

        String expected = new String(hasher.digest());

        String sha256Hashed = messageHasher.hash(RevocationType.TOKEN, message);

        assertEquals(expected, sha256Hashed);
    }

    @Test
    public void testNullHashing() throws NoSuchAlgorithmException {
        MessageHasher nullHasher = new MessageHasher(null, null);

        assertEquals(message, nullHasher.hash(RevocationType.TOKEN, message));
    }
}
