package com.example.bloodbank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDTO {
    private Long id;

    @NotBlank(message = "Hospital name is required")
    private String name;

    private String location;
    
    private String contactNumber;

    private Double latitude;

    private Double longitude;
}
