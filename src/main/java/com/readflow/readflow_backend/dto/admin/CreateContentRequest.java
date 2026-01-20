package com.readflow.readflow_backend.dto.admin;

import com.readflow.readflow_backend.entity.ContentType;
import jakarta.validation.constraints.*;

public record CreateContentRequest(
        @NotBlank String title,
        @NotBlank String body,
        @NotNull ContentType type,
        String slug,
        String excerpt,
        String coverImageUrl,
        String coverImagePublicId) {
}
