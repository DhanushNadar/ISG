package com.example.bloodbank.repository;

import com.example.bloodbank.entity.BloodCamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BloodCampRepository extends JpaRepository<BloodCamp, Long> {
    List<BloodCamp> findByHospitalIdOrderByDateDesc(Long hospitalId);
    List<BloodCamp> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date);
}
