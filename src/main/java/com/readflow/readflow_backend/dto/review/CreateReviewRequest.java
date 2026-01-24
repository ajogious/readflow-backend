package com.readflow.readflow_backend.dto.review;

import java.util.UUID;

import jakarta.validation.constraints.*;

public record CreateReviewRequest(
                @NotNull UUID contentId,
                @Min(1) @Max(5) int rating,
                @Size(max = 1000) String comment) {
}
