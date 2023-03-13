package com.sec.project.infrastructure.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.*;

import static com.sec.project.utils.Constants.*;

/**
 * Spring Boot component that will handle the Security Configurations for our application's infrastructure.
 * Asymmetric and symmetric encryption logic should be handled here and injected in other components.
 */
@Component
public class SecurityConfiguration<T> {

    /**
     * Asymmetric <Public,Private> key pair for the client service.
     */
    private final KeyPair keyPair;

    /**
     * Conversion Utils to get the byte array from a generic object.
     */
    private final ConversionUtils<T> conversionUtils;

    /**
     * Initializes the key pair defined above using the RSA algorithm, which is defined in the Constants.java file.
     *
     * @throws NoSuchAlgorithmException In case an invalid asymmetric algorithm is added to the ALGORITHM constant.
     * @see com.sec.project.utils.Constants
     */
    @Autowired
    public SecurityConfiguration(ConversionUtils<T> conversionUtils) throws NoSuchAlgorithmException {
        this.conversionUtils = conversionUtils;
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
        generator.initialize(KEY_SIZE);
        this.keyPair = generator.generateKeyPair();
    }

    /**
     * Returns the public key for the running node instance.
     *
     * @return public key from the key pair.
     */
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    /**
     * Returns the private key for the running node instance.
     *
     * @return private key from the key pair.
     */
    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    /**
     * Returns message digest for the algorithm specified in the Constants file (DIGEST_ALGORITHM).
     *
     * @return digested object.
     * @see com.sec.project.utils.Constants
     */
    public String generateMessageDigest(@NotNull T object) throws NoSuchAlgorithmException {
        return new String(MessageDigest.getInstance(DIGEST_ALGORITHM).digest(conversionUtils.convertObjectToBytes(object)));
    }
}
