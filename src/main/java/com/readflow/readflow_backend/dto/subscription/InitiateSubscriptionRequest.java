package com.readflow.readflow_backend.dto.subscription;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record InitiateSubscriptionRequest(
        @NotNull UUID planId) {
}
