package com.readflow.readflow_backend.dto.content;

import java.time.Instant;
import java.util.UUID;

import com.readflow.readflow_backend.entity.ContentStatus;
import com.readflow.readflow_backend.entity.ContentType;

public record ContentPublicResponse(
                UUID id,
                String title,
                String slug,
                String excerpt,
                String coverImageUrl,
                ContentType type,
                ContentStatus status,
                Instant createdAt,
                Instant updatedAt) {
}
