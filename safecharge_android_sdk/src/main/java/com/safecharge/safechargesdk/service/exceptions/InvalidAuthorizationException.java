package com.safecharge.safechargesdk.service.exceptions;

/**
 * Error thrown when authorization data provided to a method are invalid.
 */

public class InvalidAuthorizationException extends Exception {
    public InvalidAuthorizationException(String message) {
        super(message);
    }
}
