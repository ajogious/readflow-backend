package com.readflow.readflow_backend.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.dto.subscription.InitiateSubscriptionRequest;
import com.readflow.readflow_backend.dto.subscription.InitiateSubscriptionResponse;
import com.readflow.readflow_backend.entity.*;
import com.readflow.readflow_backend.paystack.PaystackClient;
import com.readflow.readflow_backend.repository.PaymentTransactionRepository;
import com.readflow.readflow_backend.repository.SubscriptionPlanRepository;
import com.readflow.readflow_backend.repository.UserRepository;
import com.readflow.readflow_backend.security.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaystackSubscriptionService {

    private final PaystackClient paystackClient;
    private final PaymentTransactionRepository txRepo;
    private final UserRepository userRepo;
    private final SubscriptionPlanRepository planRepo;

    @Value("${paystack.currency:NGN}")
    private String currency;

    @Value("${paystack.callback-url:}")
    private String callbackUrl;

    @Transactional
    public InitiateSubscriptionResponse initiate(AuthUser user, InitiateSubscriptionRequest req) {
        var dbUser = userRepo.findById(user.getId()).orElseThrow();

        var plan = planRepo.findById(req.planId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        if (!plan.isActive()) {
            throw new IllegalArgumentException("Plan is not active");
        }

        // amount NGN -> kobo
        long amountKobo = plan.getPrice()
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();

        String reference = "rf_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);

        PaymentTransaction tx = PaymentTransaction.builder()
                .user(dbUser)
                .plan(plan) // ✅ add this field in PaymentTransaction (recommended)
                .reference(reference)
                .amount(plan.getPrice()) // store NGN
                .currency(currency)
                .status(PaymentStatus.PENDING)
                .gateway(PaymentGateway.PAYSTACK)
                .build();

        txRepo.save(tx);

        Map<String, Object> body = Map.of(
                "email", dbUser.getEmail(),
                "amount", String.valueOf(amountKobo),
                "currency", currency,
                "reference", reference,
                "callback_url", callbackUrl,
                "metadata", Map.of(
                        "userId", dbUser.getId().toString(),
                        "planId", plan.getId().toString(),
                        "planCode", plan.getCode().name(),
                        "initiatedAt", Instant.now().toString()));

        Map<String, Object> resp = paystackClient.initializeTransaction(body);

        Object status = resp.get("status");
        if (!(status instanceof Boolean b) || !b) {
            throw new IllegalArgumentException("Paystack init failed");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) resp.get("data");

        String authorizationUrl = (String) data.get("authorization_url");
        if (authorizationUrl == null || authorizationUrl.isBlank()) {
            throw new IllegalArgumentException("Paystack init failed: authorization_url missing");
        }

        return new InitiateSubscriptionResponse(reference, authorizationUrl);
    }
}
