package com.example.bloodbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloodCampBookingDTO {
    private Long id;
    private Long campId;
    private String campTitle;
    private String campLocation;
    private LocalDate campDate;
    private LocalTime campTime;
    
    private Long patientId;
    private String patientName;
    private String patientBloodGroup;
    private String patientAadhaar;
    
    private String slipNumber;
    private LocalDateTime bookingTime;
}
