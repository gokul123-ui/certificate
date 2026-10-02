package com.example.certificate.controller;

import com.example.certificate.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/pl")
    public ResponseEntity<Map<String, Double>> getPlatformPL() {
        return ResponseEntity.ok(reportService.getPlatformPL());
    }

    @GetMapping("/budget")
    public ResponseEntity<Map<String, Double>> getUnitBudgetReport() {
        return ResponseEntity.ok(reportService.getUnitBudgetReport());
    }
}
