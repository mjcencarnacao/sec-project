package com.sec.project.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecurityConfigurationTest {

    @Autowired
    private SecurityConfiguration securityConfiguration;

    private static final String expectedDigest = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";

    @Test
    public void testKeyPair() {
        assertNotNull(securityConfiguration.getPublicKey());
        assertNotNull(securityConfiguration.getPrivateKey());
    }

}