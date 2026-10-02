package com.example.certificate.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long institutionId;
    private String packageName;
    private Double amount;
    private String status = "PENDING";
    private LocalDate invoiceDate;

    public Invoice() {
    }

    public Invoice(Long id, Long institutionId, String packageName, Double amount, String status, LocalDate invoiceDate) {
        this.id = id;
        this.institutionId = institutionId;
        this.packageName = packageName;
        this.amount = amount;
        this.status = (status != null) ? status : "PENDING";
        this.invoiceDate = invoiceDate;
    }

    @PrePersist
    public void onCreate() {
        if (this.status == null) {
            this.status = "PENDING";
        }
        if (this.invoiceDate == null) {
            this.invoiceDate = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
}
