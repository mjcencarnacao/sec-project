package com.sec.project.infrastructure.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static com.sec.project.infrastructure.repositories.KeyExchangeServiceImplementation.publicKeyPeerHashMap;
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
     * Current Symmetric Communication Key
     */
    private SecretKey symmetricKey = null;

    /**
     * Cipher objects for encryption and decryption
     */
    private final Cipher symmetricCipher;
    private final Cipher asymmetricCipher;


    /**
     * Initializes the key pair defined above using the RSA algorithm, which is defined in the Constants.java file.
     *
     * @throws NoSuchAlgorithmException In case an invalid asymmetric algorithm is added to the ALGORITHM constant.
     * @see com.sec.project.utils.Constants
     */
    @Autowired
    public SecurityConfiguration() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.symmetricCipher = Cipher.getInstance(SYMMETRIC_TRANSFORMATION_ALGORITHM);
        this.asymmetricCipher = Cipher.getInstance(ASYMMETRIC_TRANSFORMATION_ALGORITHM);
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ASYMMETRIC_ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        generator.initialize(ASYMMETRIC_KEY_SIZE, random);
        this.keyPair = generator.generateKeyPair();
    }

    public PublicKey bytesArrayToPublicKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(ASYMMETRIC_ALGORITHM).generatePublic(new X509EncodedKeySpec(bytes));
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

    public SecretKey getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(SecretKey symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public byte[] asymmetricEncoding(byte[] input) {
        try {
            asymmetricCipher.init(Cipher.ENCRYPT_MODE, getPrivateKey());
            return asymmetricCipher.doFinal(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] asymmetricDecoding(byte[] encoded, int port) {
        try {
            asymmetricCipher.init(Cipher.DECRYPT_MODE, publicKeyPeerHashMap.get(port));
            return asymmetricCipher.doFinal(encoded);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns message digest for the algorithm specified in the Constants file (DIGEST_ALGORITHM).
     *
     * @return digested object.
     * @see com.sec.project.utils.Constants
     */
    public String generateMessageDigest(@NotNull T object) throws NoSuchAlgorithmException {
        return "";
        //return new String(MessageDigest.getInstance(DIGEST_ALGORITHM).digest(conversionUtils.convertObjectToBytes(object)));
    }

    public byte[] symmetricEncoding(byte[] input) {
        try {
            symmetricCipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
            return symmetricCipher.doFinal(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] symmetricDecoding(byte[] bytes) {
        try {
            symmetricCipher.init(Cipher.DECRYPT_MODE, symmetricKey);
            return symmetricCipher.doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SecretKey getEncodedSymmetricKey() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(SYMMETRIC_ALGORITHM);
            keygen.init(SYMMETRIC_KEY_SIZE);
            return keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
