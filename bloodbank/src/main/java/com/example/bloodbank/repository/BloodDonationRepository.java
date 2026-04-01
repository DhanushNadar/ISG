package com.example.bloodbank.repository;

import com.example.bloodbank.entity.BloodDonation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodDonationRepository extends JpaRepository<BloodDonation, Long> {
    List<BloodDonation> findByPatientId(Long patientId);
}
