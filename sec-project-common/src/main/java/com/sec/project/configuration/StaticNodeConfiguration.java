package com.sec.project.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.sec.project.utils.Constants.*;

/**
 * Component that holds the static configuration allowed in the project.
 */
@Component
@ConfigurationProperties("app.nodes")
public class StaticNodeConfiguration {

    /**
     * Leader Port.
     */
    public static final int LEADER_PORT = 4000;
    /**
     * Static Ports for different nodes.
     */
    public static final List<Integer> ports = Arrays.asList(4000, 4001, 4002, 4003);

    /**
     * Faulty Nodes. Used to get the Quorum size for the given IBFT instance.
     */
    public static final int faultyNodes = 1;

    /**
     * Method that calculates the Quorum size for a given number of elements in the Blockchain.
     *
     * @return minimum messages for the quorum to take place.
     */
    public static int getQuorum() {
        return 2 * faultyNodes + 1;
    }

    /**
     * Retrieves the most recent Public Keys stored in the Shared directory.
     *
     * @return HashMap<Integer, PublicKey> mapping ports to public keys
     */
    public static HashMap<Integer, PublicKey> getPublicKeysFromFile(boolean isClient) {
        String path = isClient ? CLIENT_KEY_STORAGE : BLOCKCHAIN_KEY_STORAGE;
        HashMap<Integer, PublicKey> publicKeyHashMap = new HashMap<>();
        Arrays.stream(Objects.requireNonNull(new File(path).listFiles())).toList().forEach(file -> {
                    try {
                        publicKeyHashMap.put(Integer.parseInt(file.getName().split("\\.")[0]), bytesArrayToPublicKey(Files.readAllBytes(file.toPath())));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        return publicKeyHashMap;
    }

    public static HashMap<Integer, PublicKey> getAllPublicKeysIntegersFromFile() {
        HashMap<Integer, PublicKey> publicKeyHashMap = new HashMap<>();
        Arrays.stream(Objects.requireNonNull(new File(ROOT_KEY_STORAGE).listFiles())).toList().forEach(
                folder ->  Arrays.stream(folder.listFiles()).toList().forEach(file -> {
                    try {
                        publicKeyHashMap.put(Integer.parseInt(file.getName().split("\\.")[0]), bytesArrayToPublicKey(Files.readAllBytes(file.toPath())));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ));
        return publicKeyHashMap;
    }

    /**
     * Conversion of a byte array to a public key object.
     *
     * @param bytes containing the key.
     * @return public key from the byte array.
     */
    public static PublicKey bytesArrayToPublicKey(byte[] bytes) throws Exception {
        return KeyFactory.getInstance(ASYMMETRIC_ALGORITHM).generatePublic(new X509EncodedKeySpec(bytes));
    }

}
