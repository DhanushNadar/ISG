package com.example.bloodbank.controller;

import com.example.bloodbank.dto.BloodRecordDTO;
import com.example.bloodbank.service.BloodRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blood-records")
@RequiredArgsConstructor
public class BloodRecordController {

    private final BloodRecordService bloodRecordService;

    @PostMapping
    public ResponseEntity<BloodRecordDTO> addBloodRecord(@Valid @RequestBody BloodRecordDTO dto) {
        return new ResponseEntity<>(bloodRecordService.addBloodRecord(dto), HttpStatus.CREATED);
    }
}
