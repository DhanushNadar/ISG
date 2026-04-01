package com.example.bloodbank.service;

import com.example.bloodbank.dto.HospitalDTO;
import com.example.bloodbank.entity.Hospital;
import com.example.bloodbank.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalDTO addHospital(HospitalDTO dto) {
        Hospital hospital = Hospital.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .contactNumber(dto.getContactNumber())
                .build();
                
        hospital = hospitalRepository.save(hospital);
        dto.setId(hospital.getId());
        return dto;
    }

    public List<HospitalDTO> getAllHospitals() {
        return hospitalRepository.findAll().stream()
                .map(h -> HospitalDTO.builder()
                        .id(h.getId())
                        .name(h.getName())
                        .location(h.getLocation())
                        .contactNumber(h.getContactNumber())
                        .build())
                .collect(Collectors.toList());
    }
}
