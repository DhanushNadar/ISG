package com.example.bloodbank.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BloodRecordDTO {
    private Long id;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;

    private Double hemoglobin;
    private Double platelets;
    private Double rbc;
    private Double wbc;

    @NotNull(message = "Record Date is required")
    private LocalDate recordDate;
}
