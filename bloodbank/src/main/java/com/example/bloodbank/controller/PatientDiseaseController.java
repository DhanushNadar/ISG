package com.example.bloodbank.controller;

import com.example.bloodbank.dto.PatientDiseaseRequest;
import com.example.bloodbank.dto.PatientDiseaseResponse;
import com.example.bloodbank.entity.DiseaseStatus;
import com.example.bloodbank.service.PatientDiseaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/patient-disease")
@RequiredArgsConstructor
public class PatientDiseaseController {

    private final PatientDiseaseService patientDiseaseService;

    @PostMapping
    public ResponseEntity<PatientDiseaseResponse> assignDisease(@Valid @RequestBody PatientDiseaseRequest request) {
        return new ResponseEntity<>(patientDiseaseService.assignDisease(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDiseaseResponse> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        DiseaseStatus status = DiseaseStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(patientDiseaseService.updateStatus(id, status));
    }
}
