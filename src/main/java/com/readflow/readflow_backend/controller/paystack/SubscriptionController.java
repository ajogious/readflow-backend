package com.readflow.readflow_backend.controller.paystack;

import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.readflow.readflow_backend.dto.subscription.InitiateSubscriptionRequest;
import com.readflow.readflow_backend.dto.subscription.InitiateSubscriptionResponse;
import com.readflow.readflow_backend.dto.subscription.SubscriptionStatusResponse;
import com.readflow.readflow_backend.security.AuthUser;
import com.readflow.readflow_backend.service.PaystackSubscriptionService;
import com.readflow.readflow_backend.service.SubscriptionStatusService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
@Validated
public class SubscriptionController {

    private final PaystackSubscriptionService paystackSubscriptionService;
    private final SubscriptionStatusService statusService;

    @PostMapping("/initiate")
    public InitiateSubscriptionResponse initiate(
            Authentication authentication,
            @Valid @RequestBody InitiateSubscriptionRequest req) {
        AuthUser user = (AuthUser) authentication.getPrincipal();
        return paystackSubscriptionService.initiate(user, req);
    }

    @GetMapping("/status")
    public SubscriptionStatusResponse status(Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        return statusService.getStatus(user);
    }
}
