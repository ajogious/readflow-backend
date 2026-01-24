package com.readflow.readflow_backend.dto.review;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
                UUID id,
                UUID userId,
                UUID contentId,
                int rating,
                String comment,
                Instant createdAt) {
}
