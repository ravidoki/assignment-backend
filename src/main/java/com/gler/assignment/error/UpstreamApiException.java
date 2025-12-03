package com.gler.assignment.error;

public class UpstreamApiException extends RuntimeException {
    public UpstreamApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
