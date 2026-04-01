package com.example.bloodbank.repository;

import com.example.bloodbank.entity.BloodRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BloodRecordRepository extends JpaRepository<BloodRecord, Long> {
    List<BloodRecord> findByPatientId(Long patientId);
    
    Optional<BloodRecord> findTopByPatientIdOrderByRecordDateDesc(Long patientId);
}
