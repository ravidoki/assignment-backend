package com.gler.assignment.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for AssignmentException.
 * Tests the custom runtime exception functionality.
 */
class AssignmentExceptionTest {

    @Test
    void constructor_withMessage_shouldCreateExceptionWithMessage() {
        // Given
        String expectedMessage = "Test error message";

        // When
        AssignmentException exception = new AssignmentException(expectedMessage);

        // Then
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void constructor_withMessageAndCause_shouldCreateExceptionWithMessageAndCause() {
        // Given
        String expectedMessage = "Test error with cause";
        Throwable expectedCause = new IllegalArgumentException("Original cause");

        // When
        AssignmentException exception = new AssignmentException(expectedMessage, expectedCause);

        // Then
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
        assertThat(exception.getCause()).isEqualTo(expectedCause);
    }

    @Test
    void constructor_withCause_shouldCreateExceptionWithCause() {
        // Given
        Throwable expectedCause = new RuntimeException("Original exception");

        // When
        AssignmentException exception = new AssignmentException(expectedCause);

        // Then
        assertThat(exception.getMessage()).isEqualTo(expectedCause.toString());
        assertThat(exception.getCause()).isEqualTo(expectedCause);
    }

    @Test
    void shouldBeRuntimeException() {
        // Given & When
        AssignmentException exception = new AssignmentException("Test message");

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(RuntimeException.class.isAssignableFrom(AssignmentException.class)).isTrue();
    }
}
