package com.example.certificate.service;

import com.example.certificate.model.Invoice;
import com.example.certificate.model.Payment;
import com.example.certificate.repository.InvoiceRepository;
import com.example.certificate.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public PaymentService(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public Payment makePayment(Long invoiceId, String paymentMethod) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("invoiceId must be valid");
        }

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with ID: " + invoiceId));

        Payment payment = new Payment();
        payment.setInvoiceId(invoice.getId());
        payment.setAmount(invoice.getAmount());
        payment.setPaymentMethod((paymentMethod != null && !paymentMethod.trim().isEmpty()) ? paymentMethod : "BANK");
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus("SUCCESS");

        Payment savedPayment = paymentRepository.save(payment);

        // Update invoice status to PAID
        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);

        return savedPayment;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
