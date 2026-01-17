package com.readflow.readflow_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.readflow.readflow_backend.dto.auth.LoginRequest;
import com.readflow.readflow_backend.dto.auth.LoginResponse;
import com.readflow.readflow_backend.dto.auth.RegisterRequest;
import com.readflow.readflow_backend.dto.auth.RegisterResponse;
import com.readflow.readflow_backend.repository.UserRepository;
import com.readflow.readflow_backend.security.AuthUser;
import com.readflow.readflow_backend.security.JwtService;
import com.readflow.readflow_backend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder; // will be use later

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req.email(), req.password());
        return ResponseEntity
                .ok(new RegisterResponse("Registration successful. Check your email to verify your account."));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));

        var principal = (AuthUser) auth.getPrincipal();

        var user = userRepository.findById(principal.getId()).orElseThrow();

        String token = jwtService.generateToken(user.getId(), user.getRole().name());

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        new LoginResponse.UserSummary(user.getId().toString(), user.getEmail(),
                                user.getRole().name())));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // MVP: stateless JWT logout (client deletes token)
        return ResponseEntity.ok().build();
    }
}
