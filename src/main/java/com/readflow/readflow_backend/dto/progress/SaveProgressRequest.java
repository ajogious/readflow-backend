package com.readflow.readflow_backend.dto.progress;

import java.util.UUID;

import jakarta.validation.constraints.*;

public record SaveProgressRequest(
                @NotNull UUID contentId,
                @NotNull @Min(0) Integer position,
                @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double percent) {
}
