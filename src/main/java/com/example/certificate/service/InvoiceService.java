package com.example.certificate.service;

import com.example.certificate.model.Invoice;
import com.example.certificate.repository.InstitutionRepository;
import com.example.certificate.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InstitutionRepository institutionRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, InstitutionRepository institutionRepository) {
        this.invoiceRepository = invoiceRepository;
        this.institutionRepository = institutionRepository;
    }

    public Invoice createInvoice(Invoice invoice) {
        if (invoice.getInstitutionId() == null) {
            throw new IllegalArgumentException("institutionId must be valid");
        }
        if (!institutionRepository.existsById(invoice.getInstitutionId())) {
            throw new IllegalArgumentException("Institution not found with ID: " + invoice.getInstitutionId());
        }
        if (invoice.getAmount() == null || invoice.getAmount() <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        if (invoice.getStatus() == null || invoice.getStatus().trim().isEmpty()) {
            invoice.setStatus("PENDING");
        }
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDate.now());
        }
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with ID: " + id));
    }
}
