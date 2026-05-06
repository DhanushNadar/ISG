package com.example.bloodbank.controller;

import com.example.bloodbank.dto.PatientDTO;
import com.example.bloodbank.dto.PatientProfileResponse;
import com.example.bloodbank.dto.SetPatientPasswordRequest;
import com.example.bloodbank.service.PatientService;
import com.example.bloodbank.entity.User;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO dto, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(patientService.createPatient(dto, user), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients(@AuthenticationPrincipal User user) {
        log.info("Fetching complete patient directory for user.");
        return ResponseEntity.ok(patientService.getAllPatients(user));
    }

    @GetMapping("/{aadhaar}")
    public ResponseEntity<PatientProfileResponse> getPatientProfile(@PathVariable String aadhaar, @AuthenticationPrincipal User user) {
        log.info("Fetching patient profile for Aadhaar: {}", aadhaar);
        return ResponseEntity.ok(patientService.getPatientProfile(aadhaar, user));
    }

    @PostMapping("/{aadhaar}/emergency-access")
    public ResponseEntity<PatientProfileResponse> triggerEmergencyAccess(
            @PathVariable String aadhaar,
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal User user) {
        
        String reason = requestBody.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        log.warn("Emergency access triggered for Aadhaar: {} by User: {}", aadhaar, user.getEmail());
        return ResponseEntity.ok(patientService.triggerEmergencyAccess(aadhaar, reason, user));
    }

    @GetMapping("/{id}/eligibility")
    public ResponseEntity<Map<String, String>> checkEligibility(@PathVariable Long id) {
        String status = patientService.checkEligibility(id);
        return ResponseEntity.ok(Map.of("eligibility", status));
    }

    @PostMapping("/{aadhaar}/set-portal-password")
    public ResponseEntity<Void> setPortalPassword(
            @PathVariable String aadhaar,
            @RequestBody SetPatientPasswordRequest request) {
        log.info("Setting portal password for Aadhaar: {}", aadhaar);
        patientService.setPortalPassword(aadhaar, request.getPassword());
        return ResponseEntity.ok().build();
    }
}
