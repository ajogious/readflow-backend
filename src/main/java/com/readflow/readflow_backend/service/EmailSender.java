package com.readflow.readflow_backend.service;

public interface EmailSender {
    void send(String to, String subject, String html);
}