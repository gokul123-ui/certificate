package com.example.certificate.service;

import com.example.certificate.model.Invoice;
import com.example.certificate.repository.InvoiceRepository;
import com.example.certificate.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public ReportService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Platform P&L Report
     * totalRevenue = sum of PAID invoice amounts
     * totalPayments = sum of successful payments
     * profit = totalRevenue - totalPayments
     */
    public Map<String, Double> getPlatformPL() {
        Double totalRevenue = invoiceRepository.sumAmountByStatus("PAID");
        if (totalRevenue == null) {
            totalRevenue = 0.0;
        }

        Double totalPayments = paymentRepository.sumAmountByStatus("SUCCESS");
        if (totalPayments == null) {
            totalPayments = 0.0;
        }

        Double profit = totalRevenue - totalPayments;

        Map<String, Double> plReport = new HashMap<>();
        plReport.put("totalRevenue", totalRevenue);
        plReport.put("totalPayments", totalPayments);
        plReport.put("profit", profit);

        return plReport;
    }

    /**
     * Unit Budget Report
     * Calculates breakdown between Certificate Verification and API Billing based on PAID invoices.
     * If invoices explicitly mention package types, it sums them accordingly;
     * otherwise, allocates 40% to Certificate Verification and 60% to API Billing.
     */
    public Map<String, Double> getUnitBudgetReport() {
        List<Invoice> paidInvoices = invoiceRepository.findByStatus("PAID");
        
        double certVerificationSum = 0.0;
        double apiBillingSum = 0.0;
        double totalPaid = 0.0;

        for (Invoice invoice : paidInvoices) {
            double amt = (invoice.getAmount() != null) ? invoice.getAmount() : 0.0;
            totalPaid += amt;
            String pkg = (invoice.getPackageName() != null) ? invoice.getPackageName().toLowerCase() : "";
            if (pkg.contains("cert") || pkg.contains("verification")) {
                certVerificationSum += amt;
            } else if (pkg.contains("api") || pkg.contains("query") || pkg.contains("queries")) {
                apiBillingSum += amt;
            }
        }

        // If package names didn't explicitly match or there's an unallocated total, distribute proportionally
        double remaining = totalPaid - (certVerificationSum + apiBillingSum);
        if (remaining > 0) {
            certVerificationSum += remaining * 0.40;
            apiBillingSum += remaining * 0.60;
        }

        Map<String, Double> budgetReport = new HashMap<>();
        budgetReport.put("certificateVerification", certVerificationSum);
        budgetReport.put("apiBilling", apiBillingSum);
        budgetReport.put("total", totalPaid);

        return budgetReport;
    }
}
