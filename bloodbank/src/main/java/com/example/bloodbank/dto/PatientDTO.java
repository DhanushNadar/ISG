package com.example.bloodbank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    private Long id;

    @NotBlank(message = "Aadhaar number is required")
    @Pattern(regexp = "^[2-9]{1}[0-9]{11}$", message = "Aadhaar must be valid 12 digits")
    private String aadhaarNumber;

    @NotBlank(message = "Name is required")
    private String name;

    private Integer age;
    private String gender;
    private String bloodGroup;
    private String phone;
}
