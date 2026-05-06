package com.example.bloodbank.repository;

import com.example.bloodbank.entity.PatientPortalCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientPortalCredentialRepository extends JpaRepository<PatientPortalCredential, Long> {
    Optional<PatientPortalCredential> findByAadhaarNumber(String aadhaarNumber);
}
