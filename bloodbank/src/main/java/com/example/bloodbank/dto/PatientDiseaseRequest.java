package com.example.bloodbank.dto;

import com.example.bloodbank.entity.DiseaseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
