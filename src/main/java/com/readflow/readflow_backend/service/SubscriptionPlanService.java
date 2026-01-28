package com.readflow.readflow_backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.dto.plan.*;
import com.readflow.readflow_backend.entity.SubscriptionPlan;
import com.readflow.readflow_backend.repository.SubscriptionPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;

    @Transactional
    public PlanResponse create(CreatePlanRequest req) {
        if (planRepository.existsByCode(req.code())) {
            throw new IllegalArgumentException("Plan code already exists");
        }

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .code(req.code())
                .name(req.name())
                .price(req.price())
                .durationDays(req.durationDays())
                .active(req.active() == null ? true : req.active())
                .build();

        plan = planRepository.save(plan);
        return toResponse(plan);
    }

    @Transactional
    public PlanResponse update(UUID id, UpdatePlanRequest req) {
        var plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        // ensure unique code if changing
        if (!plan.getCode().equals(req.code()) && planRepository.existsByCode(req.code())) {
            throw new IllegalArgumentException("Plan code already exists");
        }

        plan.setCode(req.code());
        plan.setName(req.name());
        plan.setPrice(req.price());
        plan.setDurationDays(req.durationDays());
        if (req.active() != null)
            plan.setActive(req.active());

        return toResponse(plan);
    }

    @Transactional
    public PlanResponse toggle(UUID id) {
        var plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        plan.setActive(!plan.isActive());
        return toResponse(plan);
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> listAdmin() {
        return planRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> listActive() {
        return planRepository.findByActiveTrueOrderByDurationDaysAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PlanResponse toResponse(SubscriptionPlan p) {
        return new PlanResponse(
                p.getId(),
                p.getCode(),
                p.getName(),
                p.getPrice(),
                p.getDurationDays(),
                p.isActive(),
                p.getCreatedAt(),
                p.getUpdatedAt());
    }
}
