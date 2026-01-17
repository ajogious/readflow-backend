package com.readflow.readflow_backend.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.entity.EmailVerificationToken;
import com.readflow.readflow_backend.entity.User;
import com.readflow.readflow_backend.entity.UserRole;
import com.readflow.readflow_backend.entity.UserStatus;
import com.readflow.readflow_backend.repository.EmailVerificationTokenRepository;
import com.readflow.readflow_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${spring.app.webBaseUrl:http://localhost:3000}")
    private String webBaseUrl;

    private static final SecureRandom RANDOM = new SecureRandom();

    public void register(String email, String rawPassword) {
        String normalized = email.trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(normalized)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(UserRole.USER)
                .status(UserStatus.INACTIVE)
                .build();

        user = userRepository.save(user);

        String token = generateSecureToken();
        Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .user(user)
                .token(token)
                .expiresAt(expiresAt)
                .build();

        tokenRepository.save(verificationToken);

        String link = webBaseUrl + "/auth/verify-email?token=" + token;

        emailService.sendEmail(
                normalized,
                "Verify your ReadFlow account",
                "Click to verify your email: " + link + "\n\nThis link expires in 24 hours.");
    }

    @Transactional
    public void verifyEmail(String token) {
        var record = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (record.isUsed())
            throw new IllegalArgumentException("Token already used");
        if (record.getExpiresAt().isBefore(Instant.now()))
            throw new IllegalArgumentException("Token expired");

        record.getUser().setStatus(UserStatus.ACTIVE);
        record.setUsedAt(Instant.now());
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
