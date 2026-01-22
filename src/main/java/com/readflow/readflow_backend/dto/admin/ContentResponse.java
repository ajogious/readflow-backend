package com.readflow.readflow_backend.dto.admin;

import java.time.Instant;
import java.util.UUID;

import com.readflow.readflow_backend.dto.content.CategorySummary;
import com.readflow.readflow_backend.entity.*;

import java.util.List;

public record ContentResponse(
        UUID id,
        String title,
        String slug,
        String excerpt,
        String coverImageUrl,
        String coverImagePublicId,
        String body,
        ContentType type,
        ContentStatus status,
        UUID createdBy,
        Instant createdAt,
        Instant updatedAt,
        List<CategorySummary> categories) {
}
