package com.example.bloodbank.repository;

import com.example.bloodbank.entity.HospitalPatientVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalPatientVisitRepository extends JpaRepository<HospitalPatientVisit, Long> {
    List<HospitalPatientVisit> findByPatientId(Long patientId);
    List<HospitalPatientVisit> findByHospitalId(Long hospitalId);
}
