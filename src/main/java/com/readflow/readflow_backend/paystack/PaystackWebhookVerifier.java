package com.readflow.readflow_backend.paystack;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaystackWebhookVerifier {

    @Value("${paystack.secret-key}")
    private String secretKey;

    public boolean isValid(String payload, String signature) {
        if (signature == null || signature.isBlank())
            return false;

        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512Hmac.init(keySpec);

            byte[] macData = sha512Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = toHex(macData);

            return computed.equalsIgnoreCase(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
