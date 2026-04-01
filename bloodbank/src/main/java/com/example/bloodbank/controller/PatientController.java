package com.example.bloodbank.controller;

import com.example.bloodbank.dto.PatientDTO;
import com.example.bloodbank.dto.PatientProfileResponse;
import com.example.bloodbank.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO dto) {
        return new ResponseEntity<>(patientService.createPatient(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{aadhaar}")
    public ResponseEntity<PatientProfileResponse> getPatientProfile(@PathVariable String aadhaar) {
        return ResponseEntity.ok(patientService.getPatientProfile(aadhaar));
    }

    @GetMapping("/{id}/eligibility")
    public ResponseEntity<Map<String, String>> checkEligibility(@PathVariable Long id) {
        String status = patientService.checkEligibility(id);
        return ResponseEntity.ok(Map.of("eligibility", status));
    }
}
