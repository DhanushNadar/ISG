package com.example.bloodbank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blood_camp_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodCampBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_camp_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BloodCamp bloodCamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Patient patient;

    @Column(nullable = false, unique = true)
    private String slipNumber;

    @Column(nullable = false)
    private LocalDateTime bookingTime;
}
