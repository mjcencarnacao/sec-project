package com.sec.project.utils;

public class Constants {
    /**
     * SecurityConfiguration Constants
     */
    public static final int SYMMETRIC_KEY_SIZE = 256;
    public static final int ASYMMETRIC_KEY_SIZE = 2048;
    public static final String SYMMETRIC_ALGORITHM = "AES";
    public static final String ASYMMETRIC_ALGORITHM = "RSA";
    public static final String DIGEST_ALGORITHM = "SHA-256";
    public static final String SYMMETRIC_TRANSFORMATION_ALGORITHM = "AES/ECB/PKCS5Padding";
    public static final String ASYMMETRIC_TRANSFORMATION_ALGORITHM = "RSA/ECB/PKCS1Padding";

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
