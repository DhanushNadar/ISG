package com.example.bloodbank.repository;

import com.example.bloodbank.entity.BloodCampBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodCampBookingRepository extends JpaRepository<BloodCampBooking, Long> {
    List<BloodCampBooking> findByBloodCampIdOrderByBookingTimeDesc(Long campId);
    List<BloodCampBooking> findByPatientIdOrderByBookingTimeDesc(Long patientId);
    Optional<BloodCampBooking> findByBloodCampIdAndPatientId(Long campId, Long patientId);
}
