package com.sec.project.infrastructure.configuration;

import org.springframework.stereotype.Component;

import java.security.*;

import static com.sec.project.utils.Constants.ALGORITHM;
import static com.sec.project.utils.Constants.KEY_SIZE;

/**
 * Spring Boot component that will handle the Security Configurations for our application's infrastructure.
 * Asymmetric and symmetric encryption logic should be handled here and injected in other components.
 */
@Component
public class SecurityConfiguration {

    /**
     * Asymmetric <Public,Private> key pair for the client service.
     */
    private final KeyPair keyPair;

    /**
     * Initializes the key pair defined above using the RSA algorithm, which is defined in the Constants.java file.
     *
     * @throws NoSuchAlgorithmException In case an invalid asymmetric algorithm is added to the ALGORITHM constant.
     * @see com.sec.project.utils.Constants
     */
    public SecurityConfiguration() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
        generator.initialize(KEY_SIZE);
        this.keyPair = generator.generateKeyPair();
    }

    /**
     * Returns the public key for the running client instance.
     *
     * @return public key from the key pair.
     */
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    /**
     * Returns the private key for the running client instance.
     *
     * @return private key from the key pair.
     */
    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

}
