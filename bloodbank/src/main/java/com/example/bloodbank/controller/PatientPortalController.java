package com.example.bloodbank.controller;

import com.example.bloodbank.dto.PatientProfileResponse;
import com.example.bloodbank.entity.User;
import com.example.bloodbank.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
@Slf4j
public class PatientPortalController {

    private final PatientService patientService;

    @GetMapping("/me")
    public ResponseEntity<PatientProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        String aadhaarNumber = user.getUsername();
        log.info("Fetching patient portal profile for Aadhaar: {}", aadhaarNumber);
        
        return ResponseEntity.ok(patientService.getPatientProfileForPortal(aadhaarNumber));
    }
}
