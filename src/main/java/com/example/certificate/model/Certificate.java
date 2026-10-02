package com.example.certificate.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String certificateId;

    private String studentName;
    private String course;
    private Long institutionId;

    @Column(unique = true, nullable = false, length = 64)
    private String hash;

    private LocalDate issuedDate;

    public Certificate() {
    }

    public Certificate(Long id, String certificateId, String studentName, String course, Long institutionId, String hash, LocalDate issuedDate) {
        this.id = id;
        this.certificateId = certificateId;
        this.studentName = studentName;
        this.course = course;
        this.institutionId = institutionId;
        this.hash = hash;
        this.issuedDate = issuedDate;
    }

    @PrePersist
    public void onCreate() {
        if (this.issuedDate == null) {
            this.issuedDate = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }
}
