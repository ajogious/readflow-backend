package com.readflow.readflow_backend.exceptions;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> disabled(DisabledException ex, HttpServletRequest req) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "path", req.getRequestURI(),
                "code", "EMAIL_NOT_VERIFIED",
                "message", "Please verify your email before logging in");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> notFound(UsernameNotFoundException ex, HttpServletRequest req) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "path", req.getRequestURI(),
                "code", "AUTH_FAILED",
                "message", "Invalid credentials");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "path", req.getRequestURI(),
                "code", "INVALID_REQUEST",
                "message", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> general(Exception ex, HttpServletRequest req) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "path", req.getRequestURI(),
                "code", "INTERNAL_ERROR",
                "message", "Something went wrong");
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> badCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "path", req.getRequestURI(),
                "code", "AUTH_FAILED",
                "message", "Invalid email or password");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> auth(AuthenticationException ex, HttpServletRequest req) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "path", req.getRequestURI(),
                "code", "AUTH_FAILED",
                "message", "Authentication failed");
    }

}