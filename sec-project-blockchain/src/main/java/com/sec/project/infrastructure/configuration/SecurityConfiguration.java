package com.sec.project.infrastructure.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.*;

import static com.sec.project.utils.Constants.*;

@Component
public class SecurityConfiguration<T> {

    private final KeyPair keyPair;

    @Autowired
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

    public String generateMessageDigest(@NotNull T object) throws NoSuchAlgorithmException {
        return new String(MessageDigest.getInstance(DIGEST_ALGORITHM).digest(null));
    }
}
