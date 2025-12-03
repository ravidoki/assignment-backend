package com.gler.assignment.error;

/**
 * Custom runtime exception for assignment-specific errors.
 * This exception is used for application-level runtime errors that don't require
 * explicit handling by calling code (unchecked exception).
 *
 * Use this exception for:
 * - Business logic violations
 * - Data validation failures
 * - Configuration errors
 * - Unexpected application states
 */
public class AssignmentException extends RuntimeException {

    /**
     * Constructs a new AssignmentException with the specified detail message.
     *
     * @param message the detail message
     */
    public AssignmentException(String message) {
        super(message);
    }

    /**
     * Constructs a new AssignmentException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public AssignmentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new AssignmentException with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public AssignmentException(Throwable cause) {
        super(cause);
    }
}
