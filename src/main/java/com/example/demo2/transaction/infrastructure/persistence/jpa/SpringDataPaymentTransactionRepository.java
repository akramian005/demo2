package com.example.demo2.transaction.infrastructure.persistence.jpa;

import com.example.demo2.transaction.domain.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataPaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
}
