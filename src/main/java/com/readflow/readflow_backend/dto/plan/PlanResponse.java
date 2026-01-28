package com.readflow.readflow_backend.dto.plan;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.readflow.readflow_backend.entity.PlanCode;

public record PlanResponse(
        UUID id,
        PlanCode code,
        String name,
        BigDecimal price,
        Integer durationDays,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {
}