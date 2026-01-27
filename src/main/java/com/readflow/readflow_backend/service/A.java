package com.readflow.readflow_backend.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.entity.SubscriptionStatus;
import com.readflow.readflow_backend.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class A {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional(readOnly = true)
    public boolean isSubscriptionActive(UUID userId) {
        return subscriptionRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE
                        && sub.getEndDate() != null
                        && sub.getEndDate().isAfter(Instant.now()))
                .orElse(false);
    }
}
