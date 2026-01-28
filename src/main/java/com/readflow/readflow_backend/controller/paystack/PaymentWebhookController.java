package com.readflow.readflow_backend.controller.paystack;

import com.readflow.readflow_backend.service.PaystackWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentWebhookController {

    private final PaystackWebhookService webhookService;

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String rawBody,
            @RequestHeader(value = "x-paystack-signature", required = false) String signature) {
        webhookService.handle(rawBody, signature);
        return ResponseEntity.ok("ok");
    }
}
