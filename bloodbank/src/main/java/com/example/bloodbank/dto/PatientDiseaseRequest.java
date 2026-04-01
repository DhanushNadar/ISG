package com.example.bloodbank.dto;

import com.example.bloodbank.entity.DiseaseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientDiseaseRequest {
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Disease ID is required")
    private Long diseaseId;

    private LocalDate diagnosedDate;

    @NotNull(message = "Status is required")
    private DiseaseStatus status;

    private Boolean isCurrent;
}
