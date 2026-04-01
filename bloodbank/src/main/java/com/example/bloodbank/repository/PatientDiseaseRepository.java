package com.example.bloodbank.repository;

import com.example.bloodbank.entity.DiseaseStatus;
import com.example.bloodbank.entity.PatientDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientDiseaseRepository extends JpaRepository<PatientDisease, Long> {
    
    List<PatientDisease> findByPatientId(Long patientId);

    @Query("SELECT pd FROM PatientDisease pd WHERE pd.patient.id = :patientId AND pd.isCurrent = true ORDER BY pd.diagnosedDate DESC")
    List<PatientDisease> findCurrentDiseasesByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT COUNT(pd) > 0 FROM PatientDisease pd WHERE pd.patient.id = :patientId AND pd.isCurrent = true AND pd.status = :status AND pd.disease.isMajor = true")
    boolean existsActiveMajorDiseaseForPatient(@Param("patientId") Long patientId, @Param("status") DiseaseStatus status);
}
