package com.readflow.readflow_backend.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "spring.app.email.provider", havingValue = "console", matchIfMissing = true)
public class ConsoleEmailSender implements EmailSender {

    public ConsoleEmailSender() {
        System.out.println("⚠ ConsoleEmailSender ACTIVE");
    }

    @Override
    public void send(String to, String subject, String html) {
        System.out.println("==== EMAIL (CONSOLE) ====");
        System.out.println("TO: " + to);
        System.out.println("SUBJECT: " + subject);
        System.out.println("HTML:\n" + html);
        System.out.println("=========================");
    }
}