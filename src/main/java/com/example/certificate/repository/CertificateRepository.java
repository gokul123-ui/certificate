package com.example.certificate.repository;

import com.example.certificate.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByHash(String hash);
    Optional<Certificate> findByCertificateId(String certificateId);
    boolean existsByCertificateId(String certificateId);
}
