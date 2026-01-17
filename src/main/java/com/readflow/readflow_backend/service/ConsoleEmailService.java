package com.readflow.readflow_backend.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class ConsoleEmailService implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.println("==== EMAIL (DEV) ====");
        System.out.println("TO: " + to);
        System.out.println("SUBJECT: " + subject);
        System.out.println("BODY:\n" + body);
        System.out.println("=====================");
    }
}
