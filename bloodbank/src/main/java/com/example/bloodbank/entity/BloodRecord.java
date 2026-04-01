package com.example.bloodbank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "blood_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    private Double hemoglobin;
    private Double platelets;
    private Double rbc;
    private Double wbc;

    @Column(nullable = false)
    private LocalDate recordDate;
}
