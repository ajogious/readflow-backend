package com.readflow.readflow_backend.dto.subscription;

import java.time.Instant;

import com.readflow.readflow_backend.entity.SubscriptionStatus;

public record SubscriptionStatusResponse(
                SubscriptionStatus status,
                Instant startDate,
                Instant endDate,
                String planCode,
                Long daysLeft) {
}
