package com.readflow.readflow_backend.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readflow.readflow_backend.entity.*;
import com.readflow.readflow_backend.paystack.PaystackClient;
import com.readflow.readflow_backend.paystack.PaystackWebhookVerifier;
import com.readflow.readflow_backend.repository.PaymentTransactionRepository;
import com.readflow.readflow_backend.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaystackWebhookService {

    private final PaystackWebhookVerifier verifier;
    private final ObjectMapper objectMapper;
    private final PaystackClient paystackClient;

    private final PaymentTransactionRepository txRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final EmailService emailService; // your resend email service

    @Transactional
    public void handle(String rawBody, String signature) {
        // 1) verify signature
        if (!verifier.isValid(rawBody, signature)) {
            throw new SecurityException("Invalid Paystack signature");
        }

        // 2) parse json to get reference
        String reference = extractReference(rawBody);
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("Webhook missing reference");
        }

        // 3) find transaction
        PaymentTransaction tx = txRepo.findByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Unknown reference"));

        // ✅ idempotency: if already SUCCESS, do nothing
        if (tx.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        // 4) verify with Paystack API (authoritative)
        Map<String, Object> verifyResp = paystackClient.verifyTransaction(reference);

        boolean ok = Boolean.TRUE.equals(verifyResp.get("status"));
        if (!ok) {
            tx.setStatus(PaymentStatus.FAILED);
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) verifyResp.get("data");

        String payStatus = (String) data.get("status"); // "success"
        if (!"success".equalsIgnoreCase(payStatus)) {
            tx.setStatus(PaymentStatus.FAILED);
            return;
        }

        // optional: confirm amount matches expected
        // Paystack returns amount in kobo as Integer/Long
        Object amountObj = data.get("amount");
        long paidKobo = amountObj instanceof Number n ? n.longValue() : -1L;

        long expectedKobo = tx.getAmount().multiply(java.math.BigDecimal.valueOf(100)).longValueExact();
        if (paidKobo != expectedKobo) {
            tx.setStatus(PaymentStatus.FAILED);
            throw new SecurityException("Amount mismatch");
        }

        // 5) mark tx SUCCESS
        tx.setStatus(PaymentStatus.SUCCESS);

        // 6) create subscription using tx.plan.durationDays
        if (tx.getPlan() == null) {
            throw new IllegalStateException("Transaction has no plan attached");
        }

        Instant now = Instant.now();
        Instant end = now.plusSeconds((long) tx.getPlan().getDurationDays() * 24 * 60 * 60);

        // optional: expire old active subscriptions first
        subscriptionRepo.expireActiveForUser(tx.getUser().getId(), now);

        Subscription sub = Subscription.builder()
                .user(tx.getUser())
                .plan(tx.getPlan())
                .status(SubscriptionStatus.ACTIVE)
                .startDate(now)
                .endDate(end)
                .build();

        subscriptionRepo.save(sub);

        // 7) send email confirmation
        emailService.sendPaymentConfirmation(tx.getUser().getEmail(), tx.getPlan().getName(), end);
    }

    private String extractReference(String rawBody) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> root = objectMapper.readValue(rawBody, Map.class);

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) root.get("data");
            if (data == null)
                return null;

            return (String) data.get("reference");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid webhook payload");
        }
    }
}
