package com.sec.project.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.util.Base64;

import static com.sec.project.utils.Constants.*;

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
     * Signature object to sign and verify signatures.
     */
    private final Signature signature = Signature.getInstance(SIGNATURE_VERIFICATION);

    /**
     * MessageDigest object to generate the hashes of an array of bytes representing the data.
     */
    private final MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);


    /**
     * Initializes the key pair defined above using the RSA algorithm, which is defined in the Constants.java file.
     *
     * @throws NoSuchAlgorithmException In case an invalid asymmetric algorithm is added to the ALGORITHM constant.
     * @see com.sec.project.utils.Constants
     */
    @Autowired
    public SecurityConfiguration() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ASYMMETRIC_ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        generator.initialize(ASYMMETRIC_KEY_SIZE, random);
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

    public void writePublicKeyToFile(int port, boolean isClient) {
        String path = isClient ? CLIENT_KEY_STORAGE : BLOCKCHAIN_KEY_STORAGE;
        try {
            new File(path + port + ".pem").createNewFile();
            Files.write(Path.of(path + port + ".pem"), getPublicKey().getEncoded());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Signature of messages using AES and RSA.
     *
     * @param data to be signed.
     * @return signature.
     */
    public byte[] signMessage(byte[] data) {
        if (keyPair != null)
            try {
                signature.initSign(getPrivateKey());
                signature.update(data);
                return signature.sign();
            } catch (InvalidKeyException | SignatureException e) {
                throw new RuntimeException(e);
            }
        return null;
    }

    /**
     * Verifies a signature of messages using AES and RSA.
     *
     * @param key               of the sender.
     * @param data              to that was signed.
     * @param receivedSignature signature in bytes.
     * @return boolean checking if a signature is valid or not.
     */
    public boolean verifySignature(PublicKey key, byte[] data, byte[] receivedSignature) {
        if (data == null || key == null || receivedSignature == null)
            return false;
        try {
            signature.initVerify(key);
            signature.update(data);
            return signature.verify(receivedSignature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a hash string for a given array of bytes.
     *
     * @param data object converted to a byte array.
     * @return string representing the SHA-256 hash.
     */
    public String generateMessageDigest(byte[] data) {
        messageDigest.update(data);
        return Base64.getEncoder().encodeToString(messageDigest.digest());
    }

}
