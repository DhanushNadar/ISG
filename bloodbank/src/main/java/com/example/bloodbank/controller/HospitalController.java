package com.example.bloodbank.controller;

import com.example.bloodbank.dto.HospitalDTO;
import com.example.bloodbank.service.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
@Slf4j
public class HospitalController {

    private final HospitalService hospitalService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HospitalDTO> addHospital(@Valid @RequestBody HospitalDTO dto) {
        return new ResponseEntity<>(hospitalService.addHospital(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HospitalDTO>> getAllHospitals() {
        log.info("Fetching complete hospital directory.");
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }
}
