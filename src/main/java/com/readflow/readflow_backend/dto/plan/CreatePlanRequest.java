package com.readflow.readflow_backend.dto.plan;

import java.math.BigDecimal;

import com.readflow.readflow_backend.entity.PlanCode;

import jakarta.validation.constraints.*;

public record CreatePlanRequest(
        @NotNull PlanCode code,
        @NotBlank String name,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotNull @Min(1) Integer durationDays,
        Boolean active) {
}