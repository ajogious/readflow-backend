package com.readflow.readflow_backend.dto.bookmark;

import java.time.Instant;
import java.util.UUID;

public record BookmarkResponse(
        UUID id,
        UUID contentId,
        String title,
        String slug,
        String excerpt,
        String coverImageUrl,
        Instant createdAt) {
}
