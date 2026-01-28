package com.readflow.readflow_backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.readflow.readflow_backend.entity.PlanCode;
import com.readflow.readflow_backend.entity.SubscriptionPlan;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
    boolean existsByCode(PlanCode code);

    Optional<SubscriptionPlan> findByCode(PlanCode code);

    List<SubscriptionPlan> findByActiveTrueOrderByDurationDaysAsc();
}
