package com.example.certificate.repository;

import com.example.certificate.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStatus(String status);

    @Query("SELECT COALESCE(SUM(i.amount), 0.0) FROM Invoice i WHERE i.status = :status")
    Double sumAmountByStatus(@Param("status") String status);
}
