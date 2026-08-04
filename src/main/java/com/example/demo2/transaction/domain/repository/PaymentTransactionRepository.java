package com.example.demo2.transaction.domain.repository;

import com.example.demo2.transaction.domain.model.PaymentTransaction;

public interface PaymentTransactionRepository {

    PaymentTransaction save(PaymentTransaction transaction);
}
