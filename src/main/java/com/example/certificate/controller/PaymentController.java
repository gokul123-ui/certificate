package com.example.certificate.controller;

import com.example.certificate.model.Payment;
import com.example.certificate.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> makePayment(@RequestBody Map<String, Object> request) {
        Object invoiceIdObj = request.get("invoiceId");
        if (invoiceIdObj == null) {
            throw new IllegalArgumentException("invoiceId is required");
        }
        Long invoiceId = Long.parseLong(invoiceIdObj.toString());
        String paymentMethod = (String) request.getOrDefault("paymentMethod", "BANK");

        Payment payment = paymentService.makePayment(invoiceId, paymentMethod);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> list = paymentService.getAllPayments();
        return ResponseEntity.ok(list);
    }
}
