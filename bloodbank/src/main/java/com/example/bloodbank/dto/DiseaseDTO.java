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
public class DiseaseDTO {
    private Long id;

    @NotBlank(message = "Disease name is required")
    private String name;

    private Boolean isMajor;

    private String description;
}
