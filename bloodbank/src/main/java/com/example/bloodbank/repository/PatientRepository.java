package com.example.bloodbank.repository;

import com.example.bloodbank.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByAadhaarNumber(String aadhaarNumber);

    List<Patient> findByHospitalId(Long hospitalId);

    @Query("SELECT p FROM Patient p WHERE p.bloodGroup = :bloodGroup AND " +
           "p.id NOT IN (SELECT pd.patient.id FROM PatientDisease pd WHERE pd.disease.isMajor = true AND pd.status = 'ACTIVE') AND " +
           "p.id NOT IN (SELECT bd.patient.id FROM BloodDonation bd WHERE bd.donationDate > :fiftySixDaysAgo)")
    List<Patient> findEligibleDonorsByBloodGroup(@Param("bloodGroup") String bloodGroup, @Param("fiftySixDaysAgo") LocalDate fiftySixDaysAgo);
}
