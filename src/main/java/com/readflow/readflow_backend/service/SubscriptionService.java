package com.readflow.readflow_backend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    public boolean isSubscriptionActive(UUID userId) {
        return false; // later: real check from subscriptions table
    }
}
