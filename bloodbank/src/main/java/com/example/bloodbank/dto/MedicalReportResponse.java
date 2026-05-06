package com.example.bloodbank.dto;

import com.example.bloodbank.entity.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalReportResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private String aadhaarNumber;
    private Long diseaseId;
    private String diseaseName;
    private ReportStatus status;
    private String submissionDate;
}
