package com.sec.project.infrastructure.configuration;

import java.security.*;

import static com.sec.project.utils.Constants.ALGORITHM;
import static com.sec.project.utils.Constants.KEY_SIZE;

public class SecurityConfiguration {

    private final KeyPair keyPair;

    public SecurityConfiguration() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
        generator.initialize(KEY_SIZE);
        this.keyPair = generator.generateKeyPair();
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

}
