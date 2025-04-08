package com.skiconnect.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate hashed passwords for database initialization.
 * This is useful for creating initial admin users with properly hashed passwords.
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password: " + encodedPassword);
        
        // Verify the password
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Password verification: " + (matches ? "SUCCESS" : "FAILED"));
    }
} 