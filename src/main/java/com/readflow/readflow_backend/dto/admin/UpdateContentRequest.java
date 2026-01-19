package com.readflow.readflow_backend.dto.admin;

import com.readflow.readflow_backend.entity.*;

import jakarta.validation.constraints.*;

public record UpdateContentRequest(
                @NotBlank String title,
                @NotBlank String body,
                @NotNull ContentType type,
                @NotNull ContentStatus status) {
}
