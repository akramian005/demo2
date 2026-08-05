package com.example.demo2.payment.repository;

import com.example.demo2.payment.model.entity.PaymentTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository {

    PaymentTransaction save(PaymentTransaction transaction);
}
