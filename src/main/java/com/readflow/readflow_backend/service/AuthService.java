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
import com.readflow.readflow_backend.entity.PasswordResetToken;
import com.readflow.readflow_backend.entity.User;
import com.readflow.readflow_backend.entity.UserRole;
import com.readflow.readflow_backend.entity.UserStatus;
import com.readflow.readflow_backend.repository.EmailVerificationTokenRepository;
import com.readflow.readflow_backend.repository.PasswordResetTokenRepository;
import com.readflow.readflow_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
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

    public void forgotPassword(String email) {
        String normalized = email.trim().toLowerCase();

        // Always succeed (security)
        var userOpt = userRepository.findByEmailIgnoreCase(normalized);
        if (userOpt.isEmpty())
            return;

        var user = userOpt.get();

        String token = generateSecureToken();
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiresAt(expiresAt)
                .build();

        passwordResetTokenRepository.save(resetToken);

        String link = webBaseUrl + "/auth/reset-password?token=" + token;

        emailService.sendEmail(
                normalized,
                "Reset your ReadFlow password",
                "Click to reset your password: " + link + "\n\nThis link expires in 1 hour.");
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        var record = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (record.isUsed())
            throw new IllegalArgumentException("Token already used");
        if (record.getExpiresAt().isBefore(Instant.now()))
            throw new IllegalArgumentException("Token expired");

        var user = record.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));

        record.setUsedAt(Instant.now());
    }

}
