package com.readflow.readflow_backend.exceptions;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.*;
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

    // ===== Auth =====

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> disabled(DisabledException ex, HttpServletRequest req) {
        return err(req, "EMAIL_NOT_VERIFIED", "Please verify your email before logging in");
    }

    @ExceptionHandler({ UsernameNotFoundException.class, BadCredentialsException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> badCredentials(Exception ex, HttpServletRequest req) {
        return err(req, "AUTH_FAILED", "Invalid email or password");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> auth(AuthenticationException ex, HttpServletRequest req) {
        return err(req, "AUTH_FAILED", "Authentication failed");
    }

    // ===== Validation & Request shape =====

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = "Validation failed";
        FieldError first = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        if (first != null) {
            msg = first.getField() + ": " + first.getDefaultMessage();
        }
        return err(req, "VALIDATION_ERROR", msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> unreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return err(req, "INVALID_JSON", "Request body is invalid or malformed JSON");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> typeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        // handles bad UUIDs in path etc.
        return err(req, "INVALID_REQUEST", "Invalid parameter: " + ex.getName());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> missingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        return err(req, "INVALID_REQUEST", "Missing query parameter: " + ex.getParameterName());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> missingHeader(MissingRequestHeaderException ex, HttpServletRequest req) {
        return err(req, "INVALID_REQUEST", "Missing header: " + ex.getHeaderName());
    }

    // ===== Application =====

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return err(req, "INVALID_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> forbidden(SecurityException ex, HttpServletRequest req) {
        return err(req, "FORBIDDEN", ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Map<String, Object> responseStatus(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return Map.of(
                "timestamp", Instant.now().toString(),
                "path", req.getRequestURI(),
                "code", status == HttpStatus.NOT_FOUND ? "NOT_FOUND" : "REQUEST_FAILED",
                "message", ex.getReason() != null ? ex.getReason() : "Request failed");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Map<String, Object> methodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return err(req, "METHOD_NOT_ALLOWED", "Method not allowed");
    }

    // ===== Fallback =====

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> general(Exception ex, HttpServletRequest req) {
        // Optional: print stacktrace in local only
        // ex.printStackTrace();
        return err(req, "INTERNAL_ERROR", "Something went wrong");
    }
}
