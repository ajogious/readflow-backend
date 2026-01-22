package com.readflow.readflow_backend.dto.content;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.readflow.readflow_backend.entity.ContentStatus;
import com.readflow.readflow_backend.entity.ContentType;

public record ContentDetailResponse(
                UUID id,
                String title,
                String slug,
                String excerpt,
                String coverImageUrl,
                String body,
                ContentType type,
                ContentStatus status,
                UUID createdBy,
                Instant createdAt,
                Instant updatedAt,
                List<CategorySummary> categories) {
}
