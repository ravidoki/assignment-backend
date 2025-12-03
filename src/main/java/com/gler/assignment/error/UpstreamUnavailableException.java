package com.gler.assignment.error;

/**
 * Exception thrown when the upstream weather API is unreachable or returns an error.
 */
public class UpstreamUnavailableException extends RuntimeException {
    public UpstreamUnavailableException(String message) {
        super(message);
    }

    public UpstreamUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
