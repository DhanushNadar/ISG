package com.example.bloodbank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_access_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accessing_hospital_id", nullable = false)
    private Hospital accessingHospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false, length = 500)
    private String reason;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime accessTime;
}
