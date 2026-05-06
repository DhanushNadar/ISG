package com.example.bloodbank.repository;

import com.example.bloodbank.entity.MedicalReportSubmission;
import com.example.bloodbank.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalReportSubmissionRepository extends JpaRepository<MedicalReportSubmission, Long> {
    List<MedicalReportSubmission> findByStatus(ReportStatus status);
    List<MedicalReportSubmission> findByPatientIdOrderBySubmissionDateDesc(Long patientId);
}
