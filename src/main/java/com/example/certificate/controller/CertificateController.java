package com.example.certificate.controller;

import com.example.certificate.model.Certificate;
import com.example.certificate.service.CertificateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping
    public ResponseEntity<Certificate> issueCertificate(@RequestBody Certificate certificate) {
        Certificate created = certificateService.issueCertificate(certificate);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Certificate>> getAllCertificates() {
        List<Certificate> list = certificateService.getAllCertificates();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/verify/{hash}")
    public ResponseEntity<Map<String, Object>> verifyCertificate(@PathVariable String hash) {
        Map<String, Object> result = certificateService.verifyCertificate(hash);
        Boolean isValid = (Boolean) result.getOrDefault("valid", false);
        if (Boolean.TRUE.equals(isValid)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}
