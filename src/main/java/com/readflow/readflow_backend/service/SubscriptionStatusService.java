package com.readflow.readflow_backend.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.dto.subscription.SubscriptionStatusResponse;
import com.readflow.readflow_backend.entity.SubscriptionStatus;
import com.readflow.readflow_backend.repository.SubscriptionRepository;
import com.readflow.readflow_backend.security.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionStatusService {

    private final SubscriptionRepository subscriptionRepo;

    @Transactional(readOnly = true)
    public SubscriptionStatusResponse getStatus(AuthUser user) {
        var subOpt = subscriptionRepo.findTopByUserIdOrderByEndDateDesc(user.getId());

        if (subOpt.isEmpty()) {
            return new SubscriptionStatusResponse(
                    SubscriptionStatus.EXPIRED, null, null, null, null);
        }

        var sub = subOpt.get();
        Instant now = Instant.now();

        boolean active = sub.getStatus() == SubscriptionStatus.ACTIVE
                && sub.getEndDate() != null
                && sub.getEndDate().isAfter(now);

        SubscriptionStatus status = active ? SubscriptionStatus.ACTIVE : SubscriptionStatus.EXPIRED;

        Long daysLeft = null;
        if (sub.getEndDate() != null) {
            long d = Duration.between(now, sub.getEndDate()).toDays();
            daysLeft = Math.max(d, 0);
        }

        String planCode = sub.getPlan() != null ? sub.getPlan().getCode().name() : null;

        return new SubscriptionStatusResponse(
                status,
                sub.getStartDate(),
                sub.getEndDate(),
                planCode,
                daysLeft);
    }
}
