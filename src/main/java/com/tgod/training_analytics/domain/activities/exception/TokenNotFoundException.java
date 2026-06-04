package com.tgod.training_analytics.domain.activities.exception;

public class TokenNotFoundException extends RuntimeException {

    public TokenNotFoundException(String username) {
        super("No access token found for user: " + username);
    }
}
