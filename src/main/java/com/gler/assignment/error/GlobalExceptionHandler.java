package com.gler.assignment.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler mapping application exceptions to structured JSON responses.
 * Produces responses with keys: timestamp, status, error, message, path.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static final String TIMESTAMP = "timestamp";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";
    public static final String PATH = "path";
    public static final String UPSTREAM_API_UNREACHABLE = "Upstream API Unreachable";
    public static final String BAD_REQUEST = "Bad Request";
    public static final String INVALID_JSON = "Invalid JSON";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String DETAILS = "details";

    /**
     * Handle assignment-specific runtime exceptions and map to HTTP 500.
     */
    @ExceptionHandler(AssignmentException.class)
    public ResponseEntity<Map<String, Object>> handleAssignment(AssignmentException ex, HttpServletRequest req) {
        // Log the application-specific error
        log.error("Application error occurred for request {}: {}", req.getRequestURI(), ex.getMessage(), ex);

        // Build standardized error response for business logic errors
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, OffsetDateTime.now().toString());
        body.put(STATUS, 500);
        body.put(ERROR, INTERNAL_SERVER_ERROR);
        body.put(MESSAGE, ex.getMessage());
        body.put(PATH, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Handle upstream API failures and map to HTTP 502 with descriptive JSON body.
     * Use the single UpstreamUnavailableException type across the app.
     */
    @ExceptionHandler(UpstreamUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleUpstream(UpstreamUnavailableException ex, HttpServletRequest req) {
        // Log the upstream API failure with full stack trace
        log.error("Upstream API unavailable for request {}: {}", req.getRequestURI(), ex.getMessage(), ex);

        // Build standardized error response with timestamp, status, error details, and request path
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, OffsetDateTime.now().toString());
        body.put(STATUS, 502);
        body.put(ERROR, UPSTREAM_API_UNREACHABLE);
        body.put(MESSAGE, ex.getMessage());
        body.put(PATH, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    /**
     * Handle validation errors (e.g. missing or null required fields) and return 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        // Extract validation errors from binding result
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        // Log validation failures as warnings
        log.warn("Validation failed for request {}: {}", req.getRequestURI(), errors);

        // Build error response with validation details
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, OffsetDateTime.now().toString());
        body.put(STATUS, 400);
        body.put(ERROR, BAD_REQUEST);
        body.put(MESSAGE, VALIDATION_FAILED);
        body.put(DETAILS, errors);
        body.put(PATH, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handle malformed JSON (unreadable request body) as 400.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,Object>> handleUnreadable(
            HttpMessageNotReadableException ex, HttpServletRequest req) {

        // Log malformed JSON as warning
        log.warn("Invalid JSON received for request {}: {}", req.getRequestURI(), ex.getMessage());

        // Build error response for unreadable request body
        Map<String,Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, OffsetDateTime.now().toString());
        body.put(STATUS, 400);
        body.put(ERROR, BAD_REQUEST);
        body.put(MESSAGE, INVALID_JSON);
        body.put(PATH, req.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Generic fallback for anything unexpected — returns 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(Exception ex, HttpServletRequest req) {
        // Log unexpected errors as errors with full stack trace
        log.error("Unexpected error occurred for request {}: {}", req.getRequestURI(), ex.getMessage(), ex);

        // Build generic 500 error response for unhandled exceptions
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, OffsetDateTime.now().toString());
        body.put(STATUS, 500);
        body.put(ERROR, INTERNAL_SERVER_ERROR);
        body.put(MESSAGE, ex.getMessage());
        body.put(PATH, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
