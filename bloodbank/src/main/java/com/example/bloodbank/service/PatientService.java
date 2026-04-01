package com.example.bloodbank.service;

import com.example.bloodbank.dto.PatientDTO;
import com.example.bloodbank.dto.PatientDiseaseResponse;
import com.example.bloodbank.dto.PatientProfileResponse;
import com.example.bloodbank.entity.BloodRecord;
import com.example.bloodbank.entity.DiseaseStatus;
import com.example.bloodbank.entity.Patient;
import com.example.bloodbank.entity.PatientDisease;
import com.example.bloodbank.exception.ResourceNotFoundException;
import com.example.bloodbank.repository.BloodRecordRepository;
import com.example.bloodbank.repository.PatientDiseaseRepository;
import com.example.bloodbank.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientDiseaseRepository patientDiseaseRepository;
    private final BloodRecordRepository bloodRecordRepository;

    public PatientDTO createPatient(PatientDTO dto) {
        if (patientRepository.findByAadhaarNumber(dto.getAadhaarNumber()).isPresent()) {
            throw new IllegalArgumentException("Patient with this Aadhaar already exists");
        }
        
        Patient patient = Patient.builder()
                .aadhaarNumber(dto.getAadhaarNumber())
                .name(dto.getName())
                .age(dto.getAge())
                .gender(dto.getGender())
                .bloodGroup(dto.getBloodGroup())
                .phone(dto.getPhone())
                .build();
                
        patient = patientRepository.save(patient);
        dto.setId(patient.getId());
        return dto;
    }

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public String checkEligibility(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found");
        }
        boolean hasActiveMajorDisease = patientDiseaseRepository.existsActiveMajorDiseaseForPatient(id, DiseaseStatus.ACTIVE);
        return hasActiveMajorDisease ? "NOT_ELIGIBLE" : "ELIGIBLE";
    }

    public PatientProfileResponse getPatientProfile(String aadhaar) {
        Patient patient = patientRepository.findByAadhaarNumber(aadhaar)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with Aadhaar: " + aadhaar));

        // Eligibility
        String eligibility = checkEligibility(patient.getId());

        // Recent Hospital
        Optional<BloodRecord> recentRecord = bloodRecordRepository.findTopByPatientIdOrderByRecordDateDesc(patient.getId());
        String recentHospitalName = recentRecord.map(record -> record.getHospital().getName()).orElse("N/A");
        String recentTestDate = recentRecord.map(record -> record.getRecordDate().toString()).orElse("N/A");

        // Diseases
        List<PatientDisease> currentDiseases = patientDiseaseRepository.findCurrentDiseasesByPatientId(patient.getId());
        String recentDiseaseName = "N/A";
        String majorDiseaseName = "None";

        if (!currentDiseases.isEmpty()) {
            // First one is recent due to DESC order in query
            recentDiseaseName = currentDiseases.get(0).getDisease().getName();
            
            // Find a major disease among current ones
            Optional<PatientDisease> activeMajor = currentDiseases.stream()
                    .filter(pd -> pd.getDisease().getIsMajor() && pd.getStatus() == DiseaseStatus.ACTIVE)
                    .findFirst();
            if (activeMajor.isPresent()) {
                majorDiseaseName = activeMajor.get().getDisease().getName();
            }
        }

        List<PatientDiseaseResponse> history = patientDiseaseRepository.findByPatientId(patient.getId())
                .stream()
                .map(this::mapToDiseaseResponse)
                .collect(Collectors.toList());

        return PatientProfileResponse.builder()
                .name(patient.getName())
                .bloodGroup(patient.getBloodGroup() != null ? patient.getBloodGroup() : "Unknown")
                .recentHospital(recentHospitalName)
                .recentTestDate(recentTestDate)
                .recentDisease(recentDiseaseName)
                .majorDisease(majorDiseaseName)
                .eligibility(eligibility)
                .history(history)
                .build();
    }

    private PatientDTO mapToDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .aadhaarNumber(patient.getAadhaarNumber())
                .name(patient.getName())
                .age(patient.getAge())
                .gender(patient.getGender())
                .bloodGroup(patient.getBloodGroup())
                .phone(patient.getPhone())
                .build();
    }

    private PatientDiseaseResponse mapToDiseaseResponse(PatientDisease pd) {
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
