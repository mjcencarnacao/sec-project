package com.sec.project.utils;

public class Constants {
    /**
     * SecurityConfiguration Constants
     */
    public static final int ASYMMETRIC_KEY_SIZE = 2048;
    public static final String ASYMMETRIC_ALGORITHM = "RSA";
    public static final String DIGEST_ALGORITHM = "SHA-256";
    public static final String CLIENT_KEY_STORAGE = "keys/client/";
    public static final String SIGNATURE_VERIFICATION = "SHA256withRSA";

    /**
     * MessagingServiceImplementation Constants
     *
     * @see com.sec.project.infrastructure.repositories.MessagingServiceImplementation
     */
    public static final int MAX_BUFFER_SIZE = 4096;

}
