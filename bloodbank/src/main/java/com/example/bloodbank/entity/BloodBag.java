package com.example.bloodbank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_bags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodBag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bagId; // e.g., BAG-10023

    @Column(nullable = false)
    private String bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodBagStatus status;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime collectionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_hospital_id", nullable = false)
    private Hospital currentHospital;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    private BloodDonation bloodDonation;
}
