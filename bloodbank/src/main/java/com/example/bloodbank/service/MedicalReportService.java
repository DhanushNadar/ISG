package com.example.bloodbank.service;

import com.example.bloodbank.dto.MedicalReportResponse;
import com.example.bloodbank.entity.*;
import com.example.bloodbank.exception.ResourceNotFoundException;
import com.example.bloodbank.repository.DiseaseRepository;
import com.example.bloodbank.repository.MedicalReportSubmissionRepository;
import com.example.bloodbank.repository.PatientDiseaseRepository;
import com.example.bloodbank.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalReportService {

    private final MedicalReportSubmissionRepository reportRepository;
    private final PatientRepository patientRepository;
    private final DiseaseRepository diseaseRepository;
    private final PatientDiseaseRepository patientDiseaseRepository;

    @Transactional
    public MedicalReportResponse submitReport(String aadhaar, Long diseaseId, MultipartFile file) throws IOException {
        Patient patient = patientRepository.findByAadhaarNumber(aadhaar)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        Disease disease = diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found"));

        MedicalReportSubmission submission = MedicalReportSubmission.builder()
                .patient(patient)
                .disease(disease)
                .fileData(file.getBytes())
                .fileType(file.getContentType())
                .status(ReportStatus.PENDING)
                .build();

        submission = reportRepository.save(submission);
        return mapToResponse(submission);
    }

    @Transactional(readOnly = true)
    public List<MedicalReportResponse> getPendingReports() {
        return reportRepository.findByStatus(ReportStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MedicalReportResponse> getPatientReports(String aadhaar) {
        Patient patient = patientRepository.findByAadhaarNumber(aadhaar)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        return reportRepository.findByPatientIdOrderBySubmissionDateDesc(patient.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public com.example.bloodbank.dto.DiseaseDTO getNextDiseaseToClaim(String aadhaar) {
        Patient patient = patientRepository.findByAadhaarNumber(aadhaar)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // Find all ACTIVE diseases for this patient (not recovered)
        List<PatientDisease> activeDiseases = patientDiseaseRepository.findByPatientId(patient.getId()).stream()
                .filter(pd -> pd.getStatus() != DiseaseStatus.RECOVERED)
                .collect(Collectors.toList());

        // Find diseases that already have a pending report submission
        List<Long> pendingSubmissionDiseaseIds = reportRepository.findByPatientIdOrderBySubmissionDateDesc(patient.getId()).stream()
                .filter(sub -> sub.getStatus() == ReportStatus.PENDING)
                .map(sub -> sub.getDisease().getId())
                .collect(Collectors.toList());

        // Select the first active disease that doesn't have a pending report, prioritizing major diseases
        Optional<Disease> nextDiseaseToRecover = activeDiseases.stream()
                .map(PatientDisease::getDisease)
                .filter(d -> !pendingSubmissionDiseaseIds.contains(d.getId()))
                .sorted((d1, d2) -> Boolean.compare(d2.getIsMajor(), d1.getIsMajor())) // Major first
                .findFirst();

        return nextDiseaseToRecover.map(d -> com.example.bloodbank.dto.DiseaseDTO.builder()
                .id(d.getId())
                .name(d.getName())
                .isMajor(d.getIsMajor())
                .description(d.getDescription())
                .build()).orElse(null);
    }

    public MedicalReportSubmission getReportFile(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
    }

    @Transactional
    public MedicalReportResponse approveReport(Long id) {
        MedicalReportSubmission submission = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        
        submission.setStatus(ReportStatus.APPROVED);
        reportRepository.save(submission);

        // Update the existing diagnosis to RECOVERED instead of creating a new one
        Optional<PatientDisease> existingPd = patientDiseaseRepository.findByPatientId(submission.getPatient().getId()).stream()
                .filter(pd -> pd.getDisease().getId().equals(submission.getDisease().getId()))
                .filter(pd -> pd.getStatus() != DiseaseStatus.RECOVERED)
                .findFirst();

        if (existingPd.isPresent()) {
            PatientDisease pd = existingPd.get();
            pd.setStatus(DiseaseStatus.RECOVERED);
            pd.setIsCurrent(false);
            patientDiseaseRepository.save(pd);
        }

        return mapToResponse(submission);
    }

    @Transactional
    public MedicalReportResponse rejectReport(Long id) {
        MedicalReportSubmission submission = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        
        submission.setStatus(ReportStatus.REJECTED);
        reportRepository.save(submission);
        return mapToResponse(submission);
    }

    private MedicalReportResponse mapToResponse(MedicalReportSubmission submission) {
        return MedicalReportResponse.builder()
                .id(submission.getId())
                .patientId(submission.getPatient().getId())
                .patientName(submission.getPatient().getName())
                .aadhaarNumber(submission.getPatient().getAadhaarNumber())
                .diseaseId(submission.getDisease().getId())
                .diseaseName(submission.getDisease().getName())
                .status(submission.getStatus())
                .submissionDate(submission.getSubmissionDate() != null ? submission.getSubmissionDate().toString() : null)
                .build();
    }
}
