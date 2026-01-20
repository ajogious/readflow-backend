package com.readflow.readflow_backend.dto.admin;

import java.time.Instant;
import java.util.UUID;

import com.readflow.readflow_backend.entity.*;

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
                Instant updatedAt) {
}
