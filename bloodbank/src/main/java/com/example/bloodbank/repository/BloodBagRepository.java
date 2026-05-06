package com.example.bloodbank.repository;

import com.example.bloodbank.entity.BloodBag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodBagRepository extends JpaRepository<BloodBag, Long> {
    List<BloodBag> findByCurrentHospitalId(Long hospitalId);
}
