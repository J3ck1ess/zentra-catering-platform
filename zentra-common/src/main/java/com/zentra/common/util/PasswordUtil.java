package com.zentra.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for password hashing and verification
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER =
            new BCryptPasswordEncoder();

    /**
     * Hash raw password
     */
    public static String encode(String rawPassword) {

        return ENCODER.encode(rawPassword);
    }

    /**
     * Verify password
     */
    public static boolean matches(

            String rawPassword,
            String encodedPassword
    ) {

        return ENCODER.matches(rawPassword, encodedPassword);
    }

    private PasswordUtil() {
    }

}
