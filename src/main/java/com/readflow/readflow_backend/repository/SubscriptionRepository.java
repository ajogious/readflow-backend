package com.readflow.readflow_backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.readflow.readflow_backend.entity.Subscription;
import com.readflow.readflow_backend.entity.SubscriptionStatus;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findTopByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Subscription> findTopByUserIdAndStatusOrderByEndDateDesc(UUID userId, SubscriptionStatus status);
}
