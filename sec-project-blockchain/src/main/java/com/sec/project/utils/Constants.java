package com.sec.project.utils;

public class Constants {
    /**
     * SecurityConfiguration Constants
     */
    public static final int ASYMMETRIC_KEY_SIZE = 2048;
    public static final String ASYMMETRIC_ALGORITHM = "RSA";
    public static final String DIGEST_ALGORITHM = "SHA-256";
    public static final String SIGNATURE_VERIFICATION = "SHA256withRSA";

    /**
     * Minimum Transactions per Block
     */
    public static int MINIMUM_TRANSACTIONS = 10;

    /**
     * MessagingServiceImplementation Constants
     */
    public static final int MAX_BUFFER_SIZE = 4096;

    /**
     * Round Timeout Value.
     */
    public static final int DEFAULT_TIMEOUT = 5000;

    /**
     * Byzantine string.
     */
    public static final String BYZANTINE_RANDOM_STRING = "RANDOM_STRING";
}
