package com.sec.project.utils;

public class Constants {
    /**
     * SecurityConfiguration Constants
     */
    public static final int KEY_SIZE = 2048;
    public static final String ALGORITHM = "RSA";
    public static final String DIGEST_ALGORITHM = "SHA-256";

    /**
     * MessagingServiceImplementation Constants
     */
    public static final int MAX_BUFFER_SIZE = 4096;

    /**
     * Round Timeout Value.
     */
    public static final int ROUND_TIMEOUT = 5000;

    /**
     * Byzantine string.
     */
    public static final String BYZANTINE_RANDOM_STRING = "RANDOM_STRING";
}
