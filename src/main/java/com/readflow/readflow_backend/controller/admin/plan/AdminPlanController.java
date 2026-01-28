package com.readflow.readflow_backend.controller.admin.plan;

import java.util.List;
import java.util.UUID;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.readflow.readflow_backend.dto.plan.*;
import com.readflow.readflow_backend.service.SubscriptionPlanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/plans")
@Validated
public class AdminPlanController {

    private final SubscriptionPlanService planService;

    @PostMapping
    public PlanResponse create(@Valid @RequestBody CreatePlanRequest req) {
        return planService.create(req);
    }

    @PutMapping("/{id}")
    public PlanResponse update(@PathVariable UUID id, @Valid @RequestBody UpdatePlanRequest req) {
        return planService.update(id, req);
    }

    @PatchMapping("/{id}/toggle")
    public PlanResponse toggle(@PathVariable UUID id) {
        return planService.toggle(id);
    }

    @GetMapping
    public List<PlanResponse> list() {
        return planService.listAdmin();
    }
}
