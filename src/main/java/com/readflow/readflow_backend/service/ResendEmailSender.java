package com.readflow.readflow_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

@Service
@ConditionalOnProperty(prefix = "spring.app.email", name = "provider", havingValue = "resend")
public class ResendEmailSender implements EmailSender {

    private final Resend resend;
    private final String from;

    public ResendEmailSender(
            @Value("${spring.app.email.resendApiKey}") String apiKey,
            @Value("${spring.app.email.from}") String from) {

        this.resend = new Resend(apiKey);
        this.from = from;
        System.out.println("⚠ ResendEmailSender ACTIVE");
    }

    @Override
    public void send(String to, String subject, String html) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(from)
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();

            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Resend email id: " + data.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email via Resend", e);
        }
    }
}
