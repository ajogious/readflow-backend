package com.readflow.readflow_backend.paystack;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Component
public class PaystackClient {

    private final RestClient client;

    public PaystackClient(
            @Value("${paystack.base-url}") String baseUrl,
            @Value("${paystack.secret-key}") String secretKey) {
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> initializeTransaction(Map<String, Object> body) {
        return client.post()
                .uri("/transaction/initialize")
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> verifyTransaction(String reference) {
        try {
            return client.get()
                    .uri("/transaction/verify/{reference}", reference)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception ex) {
            // Paystack unavailable / network failure
            throw new ResponseStatusException(
                    BAD_GATEWAY,
                    "Unable to verify payment at the moment"
            );
        }
    }
    
}
