package com.example.bloodbank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HospitalDTO {
    private Long id;

    @NotBlank(message = "Hospital name is required")
    private String name;

    private String location;
    
    private String contactNumber;
}
