package com.example.bloodbank.service;

import com.example.bloodbank.dto.AuthRequest;
import com.example.bloodbank.dto.AuthResponse;
import com.example.bloodbank.dto.RegisterRequest;
import com.example.bloodbank.dto.PatientLoginRequest;
import com.example.bloodbank.entity.User;
import com.example.bloodbank.entity.PatientPortalCredential;
import com.example.bloodbank.repository.UserRepository;
import com.example.bloodbank.repository.PatientPortalCredentialRepository;
import com.example.bloodbank.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PatientPortalCredentialRepository patientPortalCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already taken.");
        }
        
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .hospitalId(request.getHospitalId())
                .build();
                
        repository.save(user);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        if (user.getHospitalId() != null) {
            claims.put("hospitalId", user.getHospitalId());
        }
        
        var jwtToken = jwtService.generateToken(claims, user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
                
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        if (user.getHospitalId() != null) {
            claims.put("hospitalId", user.getHospitalId());
        }
                
        var jwtToken = jwtService.generateToken(claims, user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse patientLogin(PatientLoginRequest request) {
        PatientPortalCredential credential = patientPortalCredentialRepository.findByAadhaarNumber(request.getAadhaarNumber())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Aadhaar number or password"));

        if (!passwordEncoder.matches(request.getPassword(), credential.getPassword())) {
            throw new IllegalArgumentException("Invalid Aadhaar number or password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "PATIENT");

        var jwtToken = jwtService.generateToken(claims, request.getAadhaarNumber());

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
