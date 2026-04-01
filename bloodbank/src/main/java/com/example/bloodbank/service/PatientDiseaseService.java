package com.example.bloodbank.service;

import com.example.bloodbank.dto.PatientDiseaseRequest;
import com.example.bloodbank.dto.PatientDiseaseResponse;
import com.example.bloodbank.entity.Disease;
import com.example.bloodbank.entity.Patient;
import com.example.bloodbank.entity.PatientDisease;
import com.example.bloodbank.entity.DiseaseStatus;
import com.example.bloodbank.exception.ResourceNotFoundException;
import com.example.bloodbank.repository.DiseaseRepository;
import com.example.bloodbank.repository.PatientDiseaseRepository;
import com.example.bloodbank.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PatientDiseaseService {

    private final PatientDiseaseRepository patientDiseaseRepository;
    private final PatientRepository patientRepository;
    private final DiseaseRepository diseaseRepository;

    public PatientDiseaseResponse assignDisease(PatientDiseaseRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        
        Disease disease = diseaseRepository.findById(request.getDiseaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found"));

        PatientDisease patientDisease = PatientDisease.builder()
                .patient(patient)
                .disease(disease)
                .diagnosedDate(request.getDiagnosedDate() != null ? request.getDiagnosedDate() : LocalDate.now())
                .status(request.getStatus())
                .isCurrent(request.getIsCurrent() != null ? request.getIsCurrent() : true)
                .build();
                
        patientDisease = patientDiseaseRepository.save(patientDisease);
        return mapToResponse(patientDisease);
    }
    
    public PatientDiseaseResponse updateStatus(Long id, DiseaseStatus newStatus) {
        PatientDisease pd = patientDiseaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PatientDisease mapping not found"));
        
        pd.setStatus(newStatus);
        pd = patientDiseaseRepository.save(pd);
        return mapToResponse(pd);
    }

    private PatientDiseaseResponse mapToResponse(PatientDisease pd) {
        return PatientDiseaseResponse.builder()
                .id(pd.getId())
                .diseaseName(pd.getDisease().getName())
                .isMajor(pd.getDisease().getIsMajor())
                .diagnosedDate(pd.getDiagnosedDate())
                .status(pd.getStatus())
                .isCurrent(pd.getIsCurrent())
                .build();
    }
}
