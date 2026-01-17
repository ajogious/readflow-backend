package com.readflow.readflow_backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.readflow.readflow_backend.entity.User;
import com.readflow.readflow_backend.entity.UserRole;
import com.readflow.readflow_backend.entity.UserStatus;
import com.readflow.readflow_backend.repository.UserRepository;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = System.getenv().getOrDefault("ADMIN_EMAIL", "admin@readflow.local");
            String adminPassword = System.getenv().getOrDefault("ADMIN_PASSWORD", "Admin12345!");

            if (userRepository.existsByEmailIgnoreCase(adminEmail))
                return;

            User admin = User.builder()
                    .email(adminEmail.toLowerCase())
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(UserRole.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();

            userRepository.save(admin);

            System.out.println("Seeded admin user: " + adminEmail);
        };
    }
}
