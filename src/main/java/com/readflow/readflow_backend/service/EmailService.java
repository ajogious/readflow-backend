package com.readflow.readflow_backend.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailSender emailSender;

    public void sendVerificationEmail(String email, String verifyLink) {
        emailSender.send(
                email,
                "Verify your ReadFlow account",
                EmailTemplates.verificationEmail(verifyLink));
    }

    public void sendResetPasswordEmail(String email, String resetLink) {
        emailSender.send(
                email,
                "Reset your ReadFlow password",
                EmailTemplates.resetPasswordEmail(resetLink));
    }

    public void sendPaymentConfirmation(String email, String planName, Instant end) {
        emailSender.send(
                email,
                "Payment Successful 🎉",
                EmailTemplates.paymentConfirmation(planName, end));
    }
}
