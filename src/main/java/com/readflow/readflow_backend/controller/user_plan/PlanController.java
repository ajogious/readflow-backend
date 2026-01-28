package com.readflow.readflow_backend.controller.user_plan;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.readflow.readflow_backend.dto.plan.PlanResponse;
import com.readflow.readflow_backend.service.SubscriptionPlanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {

    private final SubscriptionPlanService planService;

    @GetMapping
    public List<PlanResponse> listActive() {
        return planService.listActive();
    }
}
