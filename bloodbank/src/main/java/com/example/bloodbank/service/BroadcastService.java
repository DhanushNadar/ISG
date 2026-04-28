package com.example.bloodbank.service;

import com.example.bloodbank.entity.Hospital;
import com.example.bloodbank.entity.Patient;
import com.example.bloodbank.repository.HospitalRepository;
import com.example.bloodbank.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BroadcastService {

    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;
    private final ResendEmailService resendEmailService;

    public int broadcastEmergencyNeed(String bloodGroup, Long hospitalId) {
        log.info("Initiating emergency broadcast for {} blood at hospital ID: {}", bloodGroup, hospitalId);

        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        LocalDate fiftySixDaysAgo = LocalDate.now().minusDays(56);
        List<Patient> eligibleDonors = patientRepository.findEligibleDonorsByBloodGroup(bloodGroup, fiftySixDaysAgo);

        log.info("Found {} highly eligible donors for broadcast.", eligibleDonors.size());

        int sentCount = 0;
        for (Patient donor : eligibleDonors) {
            if (donor.getEmail() != null && !donor.getEmail().isEmpty()) {
                resendEmailService.sendEmergencyBroadcast(donor.getEmail(), donor.getName(), bloodGroup, hospital.getName());
                sentCount++;
            } else {
                log.warn("Skipping donor ID {}: No email address on file.", donor.getId());
            }
        }

        return sentCount;
    }
}
