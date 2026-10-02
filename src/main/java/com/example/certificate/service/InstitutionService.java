package com.example.certificate.service;

import com.example.certificate.model.Institution;
import com.example.certificate.repository.InstitutionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class   InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public Institution createInstitution(Institution institution) {
        if (institution.getName() == null || institution.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Institution name cannot be empty.");
        }
        if (institution.getEmail() == null || institution.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Institution email cannot be empty.");
        }
        if (institution.getCreatedAt() == null) {
            institution.setCreatedAt(LocalDateTime.now());
        }
        return institutionRepository.save(institution);
    }

    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    public Institution getInstitutionById(Long id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Institution not found with ID: " + id));
    }
}
