package com.readflow.readflow_backend.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_plans", indexes = {
        @Index(name = "idx_plan_code", columnList = "code", unique = true),
        @Index(name = "idx_plan_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private PlanCode code; // WEEKLY/MONTHLY/YEARLY

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price; // NGN

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays; // 7/30/365

    @Column(nullable = false)
    private boolean active;
}
