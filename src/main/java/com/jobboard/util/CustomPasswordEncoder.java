package com.jobboard.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder {
    
    private final BCryptPasswordEncoder bcryptEncoder;
    
    public CustomPasswordEncoder() {
        this.bcryptEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return bcryptEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Check if the encoded password starts with BCrypt identifier
        if (encodedPassword.startsWith("$2a$")) {
            return bcryptEncoder.matches(rawPassword, encodedPassword);
        } else {
            // For plain text passwords (admin)
            return rawPassword.toString().equals(encodedPassword);
        }
    }
}