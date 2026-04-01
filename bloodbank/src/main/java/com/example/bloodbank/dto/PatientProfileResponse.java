package com.example.bloodbank.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PatientProfileResponse {
    private String name;
    private String bloodGroup;
    private String recentHospital;
    private String recentTestDate;
    private String recentDisease;
    private String majorDisease;
    private String eligibility; // "ELIGIBLE" or "NOT_ELIGIBLE"
    private List<PatientDiseaseResponse> history;
}
