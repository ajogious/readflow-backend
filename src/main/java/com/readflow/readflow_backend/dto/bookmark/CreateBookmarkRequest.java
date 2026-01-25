package com.readflow.readflow_backend.dto.bookmark;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreateBookmarkRequest(
        @NotNull UUID contentId) {
}
