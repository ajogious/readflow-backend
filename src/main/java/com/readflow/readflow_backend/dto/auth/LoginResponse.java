package com.readflow.readflow_backend.dto.auth;

public record LoginResponse(
        String token,
        UserSummary user) {
    public record UserSummary(String id, String email, String role) {
    }
}
