package com.example.certificate.service;

import com.example.certificate.model.Certificate;
import com.example.certificate.model.Institution;
import com.example.certificate.repository.CertificateRepository;
import com.example.certificate.repository.InstitutionRepository;
import com.example.certificate.util.HashUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final InstitutionRepository institutionRepository;

    public CertificateService(CertificateRepository certificateRepository, InstitutionRepository institutionRepository) {
        this.certificateRepository = certificateRepository;
        this.institutionRepository = institutionRepository;
    }

    public Certificate issueCertificate(Certificate certRequest) {
        if (certRequest.getStudentName() == null || certRequest.getStudentName().trim().isEmpty()) {
            throw new IllegalArgumentException("studentName cannot be empty");
        }
        if (certRequest.getCourse() == null || certRequest.getCourse().trim().isEmpty()) {
            throw new IllegalArgumentException("course cannot be empty");
        }
        if (certRequest.getInstitutionId() == null) {
            throw new IllegalArgumentException("institutionId must be valid");
        }

        // Validate institution existence
        Institution institution = institutionRepository.findById(certRequest.getInstitutionId())
                .orElseThrow(() -> new IllegalArgumentException("Institution not found with ID: " + certRequest.getInstitutionId()));

        // Generate Certificate ID (e.g. CERT-1001, CERT-1002...)
        long count = certificateRepository.count();
        long nextSeq = 1001 + count;
        String generatedCertId = "CERT-" + nextSeq;
        while (certificateRepository.existsByCertificateId(generatedCertId)) {
            nextSeq++;
            generatedCertId = "CERT-" + nextSeq;
        }

        certRequest.setCertificateId(generatedCertId);
        certRequest.setIssuedDate(LocalDate.now());

        // Generate SHA-256 Hash using certificateId + studentName + course + institutionId
        String inputForHash = generatedCertId + certRequest.getStudentName() + certRequest.getCourse() + certRequest.getInstitutionId();
        String generatedHash = HashUtil.generateSHA256(inputForHash);
        certRequest.setHash(generatedHash);

        return certificateRepository.save(certRequest);
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public Map<String, Object> verifyCertificate(String hash) {
        Map<String, Object> result = new HashMap<>();
        if (hash == null || hash.trim().isEmpty()) {
            result.put("valid", false);
            result.put("message", "Certificate is invalid or not found");
            return result;
        }

        Optional<Certificate> optionalCert = certificateRepository.findByHash(hash.trim());
        if (optionalCert.isPresent()) {
            Certificate cert = optionalCert.get();
            String institutionName = "Unknown Institution";
            if (cert.getInstitutionId() != null) {
                Optional<Institution> inst = institutionRepository.findById(cert.getInstitutionId());
                if (inst.isPresent()) {
                    institutionName = inst.get().getName();
                }
            }

            result.put("valid", true);
            result.put("certificateId", cert.getCertificateId());
            result.put("studentName", cert.getStudentName());
            result.put("course", cert.getCourse());
            result.put("institution", institutionName);
            result.put("hash", cert.getHash());
            result.put("issuedDate", cert.getIssuedDate() != null ? cert.getIssuedDate().toString() : "");
        } else {
            result.put("valid", false);
            result.put("message", "Certificate is invalid or not found");
        }
        return result;
    }
}
