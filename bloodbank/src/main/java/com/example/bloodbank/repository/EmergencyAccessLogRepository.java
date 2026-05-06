package com.example.bloodbank.repository;

import com.example.bloodbank.entity.EmergencyAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyAccessLogRepository extends JpaRepository<EmergencyAccessLog, Long> {
    List<EmergencyAccessLog> findByPatientId(Long patientId);
    List<EmergencyAccessLog> findByAccessingHospitalId(Long hospitalId);
}
