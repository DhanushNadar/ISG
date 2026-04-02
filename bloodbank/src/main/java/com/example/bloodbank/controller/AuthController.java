package com.example.bloodbank.controller;

import com.example.bloodbank.dto.AuthRequest;
import com.example.bloodbank.dto.AuthResponse;
import com.example.bloodbank.dto.RegisterRequest;
import com.example.bloodbank.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        log.info("Received request to register new user: {}", request.getEmail());
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody AuthRequest request
    ) {
        log.info("Received login attempt for user: {}", request.getEmail());
        return ResponseEntity.ok(service.authenticate(request));
    }
}
