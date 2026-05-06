package com.example.bloodbank.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateCampRequest {
    private String title;
    private String location;
    private LocalDate date;
    private LocalTime time;
    private String description;
}
