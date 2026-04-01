package com.example.bloodbank.service;

import com.example.bloodbank.dto.BloodRecordDTO;
import com.example.bloodbank.entity.BloodRecord;
import com.example.bloodbank.entity.Hospital;
import com.example.bloodbank.entity.Patient;
import com.example.bloodbank.exception.ResourceNotFoundException;
import com.example.bloodbank.repository.BloodRecordRepository;
import com.example.bloodbank.repository.HospitalRepository;
import com.example.bloodbank.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BloodRecordService {

    private final BloodRecordRepository bloodRecordRepository;
    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;

    public BloodRecordDTO addBloodRecord(BloodRecordDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Hospital hospital = hospitalRepository.findById(dto.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        BloodRecord record = BloodRecord.builder()
                .patient(patient)
                .hospital(hospital)
                .hemoglobin(dto.getHemoglobin())
                .platelets(dto.getPlatelets())
                .rbc(dto.getRbc())
                .wbc(dto.getWbc())
                .recordDate(dto.getRecordDate())
                .build();
        
        record = bloodRecordRepository.save(record);
        dto.setId(record.getId());
        return dto;
    }
}
