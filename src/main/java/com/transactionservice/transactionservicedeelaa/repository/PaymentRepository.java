package com.transactionservice.transactionservicedeelaa.repository;

import com.transactionservice.transactionservicedeelaa.entities.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentDetail, String> {
}
