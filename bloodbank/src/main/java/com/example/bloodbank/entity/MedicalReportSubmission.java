package com.example.bloodbank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_report_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalReportSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;

    @Lob
    @Column(nullable = false)
    private byte[] fileData;

    @Column(nullable = false)
    private String fileType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime submissionDate;
}
