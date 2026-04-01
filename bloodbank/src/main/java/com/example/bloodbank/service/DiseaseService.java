package com.example.bloodbank.service;

import com.example.bloodbank.dto.DiseaseDTO;
import com.example.bloodbank.entity.Disease;
import com.example.bloodbank.repository.DiseaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;

    public DiseaseDTO addDisease(DiseaseDTO dto) {
        if (diseaseRepository.findByNameIgnoreCase(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Disease with name already exists");
        }
        Disease disease = Disease.builder()
                .name(dto.getName())
                .isMajor(dto.getIsMajor() != null ? dto.getIsMajor() : false)
                .description(dto.getDescription())
                .build();
        
        disease = diseaseRepository.save(disease);
        
        dto.setId(disease.getId());
        return dto;
    }

    public List<DiseaseDTO> getAllDiseases() {
        return diseaseRepository.findAll().stream()
                .map(d -> DiseaseDTO.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .isMajor(d.getIsMajor())
                        .description(d.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
