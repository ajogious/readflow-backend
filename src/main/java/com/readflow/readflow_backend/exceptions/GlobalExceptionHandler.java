package com.readflow.readflow_backend.exceptions;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> err(HttpServletRequest req, String code, String message) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "path", req.getRequestURI(),
                "code", code,
                "message", message);
    }

    // =========================
    // AUTH
    // =========================

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> disabled(
            DisabledException ex,
            HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(err(req, "EMAIL_NOT_VERIFIED", "Please verify your email before logging in"));
    }

    @ExceptionHandler({ UsernameNotFoundException.class, BadCredentialsException.class })
    public ResponseEntity<Map<String, Object>> badCredentials(
            Exception ex,
            HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(err(req, "AUTH_FAILED", "Invalid email or password"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> auth(
            AuthenticationException ex,
            HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(err(req, "AUTH_FAILED", "Authentication failed"));
    }

    // =========================
    // VALIDATION & REQUEST SHAPE
    // =========================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(
            MethodArgumentNotValidException ex,
            HttpServletRequest req) {
        String message = "Validation failed";

        FieldError first = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .orElse(null);

        if (first != null) {
            message = first.getField() + ": " + first.getDefaultMessage();
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(err(req, "VALIDATION_ERROR", message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> unreadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(err(req, "INVALID_JSON", "Request body is invalid or malformed JSON"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> typeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(err(req, "INVALID_REQUEST", "Invalid parameter: " + ex.getName()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> missingParam(
            MissingServletRequestParameterException ex,
            HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(err(req, "INVALID_REQUEST", "Missing query parameter: " + ex.getParameterName()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> missingHeader(
            MissingRequestHeaderException ex,
            HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(err(req, "INVALID_REQUEST", "Missing header: " + ex.getHeaderName()));
    }

    // =========================
    // APPLICATION
    // =========================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> badRequest(
            IllegalArgumentException ex,
            HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(err(req, "INVALID_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> forbidden(
            SecurityException ex,
            HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(err(req, "FORBIDDEN", ex.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> responseStatus(
            ResponseStatusException ex,
            HttpServletRequest req) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        String code = status == HttpStatus.NOT_FOUND
                ? "NOT_FOUND"
                : "REQUEST_FAILED";

        String message = ex.getReason() != null
                ? ex.getReason()
                : "Request failed";

        return ResponseEntity
                .status(status)
                .body(err(req, code, message));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> methodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(err(req, "METHOD_NOT_ALLOWED", "Method not allowed"));
    }

    // =========================
    // FALLBACK
    // =========================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> general(
            Exception ex,
            HttpServletRequest req) {
        // Optional: log.error("Unhandled exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(err(req, "INTERNAL_ERROR", "Something went wrong"));
    }
}
