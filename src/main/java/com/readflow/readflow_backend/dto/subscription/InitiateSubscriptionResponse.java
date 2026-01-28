package com.readflow.readflow_backend.dto.subscription;

public record InitiateSubscriptionResponse(
                String reference,
                String authorizationUrl) {
}
