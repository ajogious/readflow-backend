package com.readflow.readflow_backend.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.readflow.readflow_backend.entity.Subscription;
import com.readflow.readflow_backend.entity.SubscriptionStatus;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findTopByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Subscription> findTopByUserIdOrderByEndDateDesc(UUID userId);

    Optional<Subscription> findTopByUserIdAndStatusOrderByEndDateDesc(UUID userId, SubscriptionStatus status);

    boolean existsByUserIdAndStatusAndEndDateAfter(UUID userId, SubscriptionStatus active, Instant now);

    @Modifying
    @Query("""
               update Subscription s
               set s.status = com.readflow.readflow_backend.entity.SubscriptionStatus.EXPIRED
               where s.user.id = :userId and s.status = com.readflow.readflow_backend.entity.SubscriptionStatus.ACTIVE
                 and s.endDate <= :now
            """)
    int expireActiveForUser(UUID userId, Instant now);

}
