package com.example.bloodbank.controller;

import com.example.bloodbank.dto.DiseaseDTO;
import com.example.bloodbank.service.DiseaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
@RequiredArgsConstructor
public class DiseaseController {

    private final DiseaseService diseaseService;

    @PostMapping
    public ResponseEntity<DiseaseDTO> addDisease(@Valid @RequestBody DiseaseDTO dto) {
        return new ResponseEntity<>(diseaseService.addDisease(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DiseaseDTO>> getAllDiseases() {
        return ResponseEntity.ok(diseaseService.getAllDiseases());
    }
}
