package com.tgod.training_analytics.domain.activities.exception;

public class InvalidOAuthStateException extends RuntimeException {

    public InvalidOAuthStateException() {
        super("Invalid or expired OAuth state parameter");
    }
}
