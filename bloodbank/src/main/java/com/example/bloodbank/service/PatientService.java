package com.example.bloodbank.service;

import com.example.bloodbank.dto.PatientDTO;
import com.example.bloodbank.dto.PatientDiseaseResponse;
import com.example.bloodbank.dto.PatientProfileResponse;
import com.example.bloodbank.entity.BloodRecord;
import com.example.bloodbank.entity.DiseaseStatus;
import com.example.bloodbank.entity.Patient;
import com.example.bloodbank.entity.Hospital;
import com.example.bloodbank.entity.User;
import com.example.bloodbank.entity.Role;
import com.example.bloodbank.entity.PatientDisease;
import com.example.bloodbank.exception.ResourceNotFoundException;
import com.example.bloodbank.repository.BloodRecordRepository;
import com.example.bloodbank.repository.HospitalRepository;
import com.example.bloodbank.repository.PatientDiseaseRepository;
import com.example.bloodbank.repository.PatientRepository;
import com.example.bloodbank.repository.EmergencyAccessLogRepository;
import com.example.bloodbank.repository.PatientPortalCredentialRepository;
import com.example.bloodbank.entity.EmergencyAccessLog;
import com.example.bloodbank.entity.PatientPortalCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final HospitalRepository hospitalRepository;
    private final EmergencyAccessLogRepository emergencyAccessLogRepository;
    private final PatientPortalCredentialRepository patientPortalCredentialRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientDTO createPatient(PatientDTO dto, User user) {
        Optional<Patient> existingPatientOpt = patientRepository.findByAadhaarNumber(dto.getAadhaarNumber());
        if (existingPatientOpt.isPresent()) {
            return mapToDTO(existingPatientOpt.get());
        }
        
        Patient patient = Patient.builder()
                .aadhaarNumber(dto.getAadhaarNumber())
                .name(dto.getName())
                .age(dto.getAge())
                .gender(dto.getGender())
                .bloodGroup(dto.getBloodGroup())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();
                
        if (user.getRole() == Role.HOSPITAL && user.getHospitalId() != null) {
            Hospital h = hospitalRepository.findById(user.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
            patient.setHospital(h);
        }
                
        patient = patientRepository.save(patient);
        dto.setId(patient.getId());
        return dto;
    }

    public List<PatientDTO> getAllPatients(User user) {
        List<Patient> patients;
        if (user.getRole() == Role.HOSPITAL && user.getHospitalId() != null) {
            patients = patientRepository.findByHospitalId(user.getHospitalId());
        } else {
            patients = patientRepository.findAll();
        }
        
        return patients.stream()
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

    public PatientProfileResponse getPatientProfile(String aadhaar, User user) {
        Patient patient = patientRepository.findByAadhaarNumber(aadhaar)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with Aadhaar: " + aadhaar));

        // Security Check: If it's a hospital, they must be the registering hospital OR have an emergency access log
        if (user.getRole() == Role.HOSPITAL && user.getHospitalId() != null) {
            boolean isRegisteringHospital = patient.getHospital() != null && patient.getHospital().getId().equals(user.getHospitalId());
            if (!isRegisteringHospital) {
                // Check if they have an emergency access log
                boolean hasEmergencyAccess = emergencyAccessLogRepository.findByPatientId(patient.getId())
                        .stream()
                        .anyMatch(log -> log.getAccessingHospital().getId().equals(user.getHospitalId()));
                
                if (!hasEmergencyAccess) {
                    throw new SecurityException("EMERGENCY_ACCESS_REQUIRED");
                }
            }
        }

        return buildProfileResponse(patient);
    }

    public PatientProfileResponse getPatientProfileForPortal(String aadhaar) {
        Patient patient = patientRepository.findByAadhaarNumber(aadhaar)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        return buildProfileResponse(patient);
    }

    private PatientProfileResponse buildProfileResponse(Patient patient) {
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

    public PatientProfileResponse triggerEmergencyAccess(String aadhaar, String reason, User user) {
        if (user.getRole() != Role.HOSPITAL || user.getHospitalId() == null) {
            throw new SecurityException("Only hospitals can trigger emergency access");
        }

        Patient patient = patientRepository.findByAadhaarNumber(aadhaar)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Hospital hospital = hospitalRepository.findById(user.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        EmergencyAccessLog log = EmergencyAccessLog.builder()
                .patient(patient)
                .accessingHospital(hospital)
                .reason(reason)
                .build();
        
        emergencyAccessLogRepository.save(log);

        return getPatientProfile(aadhaar, user);
    }

    public void setPortalPassword(String aadhaar, String rawPassword) {
        patientRepository.findByAadhaarNumber(aadhaar)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        PatientPortalCredential cred = patientPortalCredentialRepository.findByAadhaarNumber(aadhaar)
                .orElse(PatientPortalCredential.builder().aadhaarNumber(aadhaar).build());

        cred.setPassword(passwordEncoder.encode(rawPassword));
        patientPortalCredentialRepository.save(cred);
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
                .email(patient.getEmail())
                .hospitalId(patient.getHospital() != null ? patient.getHospital().getId() : null)
                .hospitalName(patient.getHospital() != null ? patient.getHospital().getName() : null)
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
