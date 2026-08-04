package com.example.demo2.transaction.infrastructure.persistence;

import com.example.demo2.transaction.domain.model.PaymentTransaction;
import com.example.demo2.transaction.domain.repository.PaymentTransactionRepository;
import com.example.demo2.transaction.infrastructure.persistence.jpa.SpringDataPaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentTransactionRepositoryImpl implements PaymentTransactionRepository {

    private final SpringDataPaymentTransactionRepository springDataRepository;

    @Override
    public PaymentTransaction save(PaymentTransaction transaction) {
        return springDataRepository.save(transaction);
    }
}
