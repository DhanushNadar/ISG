package com.example.bloodbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloodCampDTO {
    private Long id;
    private Long hospitalId;
    private String hospitalName;
    private String title;
    private String location;
    private LocalDate date;
    private LocalTime time;
    private String description;
}
