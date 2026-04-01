package com.example.bloodbank.dto;

import com.example.bloodbank.entity.DiseaseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PatientDiseaseResponse {
    private Long id;
    private String diseaseName;
    private Boolean isMajor;
    private LocalDate diagnosedDate;
    private DiseaseStatus status;
    private Boolean isCurrent;
}
