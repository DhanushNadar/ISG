package com.example.bloodbank.service;

import com.example.bloodbank.dto.BloodCampBookingDTO;
import com.example.bloodbank.dto.BloodCampDTO;
import com.example.bloodbank.dto.CreateCampRequest;
import com.example.bloodbank.entity.*;
import com.example.bloodbank.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BloodCampService {

    private final BloodCampRepository bloodCampRepository;
    private final BloodCampBookingRepository bloodCampBookingRepository;
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public BloodCampDTO createCamp(String hospitalEmail, CreateCampRequest request) {
        User user = userRepository.findByEmail(hospitalEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Hospital hospital = hospitalRepository.findById(user.getHospitalId()).orElseThrow(() -> new RuntimeException("Hospital not found"));

        BloodCamp camp = BloodCamp.builder()
                .hospital(hospital)
                .title(request.getTitle())
                .location(request.getLocation())
                .date(request.getDate())
                .time(request.getTime())
                .description(request.getDescription())
                .build();

        camp = bloodCampRepository.save(camp);
        log.info("Created Blood Camp: {} by Hospital: {}", camp.getTitle(), hospital.getName());
        return mapToDTO(camp);
    }

    public List<BloodCampDTO> getAllUpcomingCamps() {
        return bloodCampRepository.findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now())
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<BloodCampDTO> getHospitalCamps(String hospitalEmail) {
        User user = userRepository.findByEmail(hospitalEmail).orElseThrow(() -> new RuntimeException("User not found"));
        return bloodCampRepository.findByHospitalIdOrderByDateDesc(user.getHospitalId())
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public BloodCampBookingDTO bookSlot(String patientAadhaar, Long campId) {
        Patient patient = patientRepository.findByAadhaarNumber(patientAadhaar).orElseThrow(() -> new RuntimeException("Patient not found"));
        BloodCamp camp = bloodCampRepository.findById(campId).orElseThrow(() -> new RuntimeException("Blood Camp not found"));

        // Check eligibility
        boolean isEligible = patient.getPatientDiseases().stream()
                .noneMatch(pd -> pd.getDisease().getIsMajor() && pd.getStatus() != DiseaseStatus.RECOVERED);

        if (!isEligible) {
            throw new RuntimeException("Patient is not eligible to donate blood");
        }

        if (bloodCampBookingRepository.findByBloodCampIdAndPatientId(campId, patient.getId()).isPresent()) {
            throw new RuntimeException("You have already booked a slot for this camp.");
        }

        String slipNumber = "CAMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        BloodCampBooking booking = BloodCampBooking.builder()
                .bloodCamp(camp)
                .patient(patient)
                .slipNumber(slipNumber)
                .bookingTime(LocalDateTime.now())
                .build();

        booking = bloodCampBookingRepository.save(booking);
        log.info("Patient {} booked slot for Camp {}. Slip: {}", patient.getAadhaarNumber(), camp.getTitle(), slipNumber);
        return mapToBookingDTO(booking);
    }

    public List<BloodCampBookingDTO> getHospitalCampBookings(String hospitalEmail, Long campId) {
        User user = userRepository.findByEmail(hospitalEmail).orElseThrow(() -> new RuntimeException("User not found"));
        BloodCamp camp = bloodCampRepository.findById(campId).orElseThrow(() -> new RuntimeException("Camp not found"));
        
        if (!camp.getHospital().getId().equals(user.getHospitalId())) {
            throw new RuntimeException("Unauthorized: Not the organizer of this camp");
        }

        return bloodCampBookingRepository.findByBloodCampIdOrderByBookingTimeDesc(campId)
                .stream().map(this::mapToBookingDTO).collect(Collectors.toList());
    }

    public List<BloodCampBookingDTO> getPatientBookings(String patientAadhaar) {
        Patient patient = patientRepository.findByAadhaarNumber(patientAadhaar).orElseThrow(() -> new RuntimeException("Patient not found"));
        return bloodCampBookingRepository.findByPatientIdOrderByBookingTimeDesc(patient.getId())
                .stream().map(this::mapToBookingDTO).collect(Collectors.toList());
    }

    private BloodCampDTO mapToDTO(BloodCamp camp) {
        return BloodCampDTO.builder()
                .id(camp.getId())
                .hospitalId(camp.getHospital().getId())
                .hospitalName(camp.getHospital().getName())
                .title(camp.getTitle())
                .location(camp.getLocation())
                .date(camp.getDate())
                .time(camp.getTime())
                .description(camp.getDescription())
                .build();
    }

    private BloodCampBookingDTO mapToBookingDTO(BloodCampBooking booking) {
        return BloodCampBookingDTO.builder()
                .id(booking.getId())
                .campId(booking.getBloodCamp().getId())
                .campTitle(booking.getBloodCamp().getTitle())
                .campLocation(booking.getBloodCamp().getLocation())
                .campDate(booking.getBloodCamp().getDate())
                .campTime(booking.getBloodCamp().getTime())
                .patientId(booking.getPatient().getId())
                .patientName(booking.getPatient().getName())
                .patientBloodGroup(booking.getPatient().getBloodGroup())
                .patientAadhaar(booking.getPatient().getAadhaarNumber())
                .slipNumber(booking.getSlipNumber())
                .bookingTime(booking.getBookingTime())
                .build();
    }
}
