package com.readflow.readflow_backend.dto.progress;

import java.time.Instant;
import java.util.UUID;

public record ProgressResponse(
                UUID contentId,
                Integer position,
                Double percent,
                Instant updatedAt) {
}
