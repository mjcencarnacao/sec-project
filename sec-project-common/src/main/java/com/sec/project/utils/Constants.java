package com.sec.project.utils;

public class Constants {
    /**
     * SecurityConfiguration Constants
     */
    public static final int ASYMMETRIC_KEY_SIZE = 2048;
    public static final String ASYMMETRIC_ALGORITHM = "RSA";
    public static final String DIGEST_ALGORITHM = "SHA-256";
    public static final String ROOT_KEY_STORAGE = "keys/";
    public static final String CLIENT_KEY_STORAGE = "keys/client/";
    public static final String SIGNATURE_VERIFICATION = "SHA256withRSA";
    public static final String BLOCKCHAIN_KEY_STORAGE = "keys/blockchain/";

    /**
     * Minimum Transactions per Block
     */
    public static int MINIMUM_TRANSACTIONS = 5;

    /**
     * Minimum Transactions Fee.
     */
    public static int MINIMUM_TRANSACTION_FEE = 1;

    /**
     * MessagingServiceImplementation Constants
     */
    public static final int MAX_BUFFER_SIZE = 99999999;

    /**
     * Round Timeout Value.
     */
    public static final int DEFAULT_TIMEOUT = 15000;

    /**
     * Byzantine string.
     */
    public static final String BYZANTINE_RANDOM_STRING = "RANDOM_STRING";
}
