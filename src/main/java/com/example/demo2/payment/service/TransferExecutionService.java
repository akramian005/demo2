package com.example.demo2.payment.service;

import com.example.demo2.payment.dto.MoneyTransferResult;
import com.example.demo2.payment.model.entity.BankAccount;
import com.example.demo2.payment.model.entity.PaymentTransaction;
import com.example.demo2.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferExecutionService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    public MoneyTransferResult transfer(
            BankAccount sourceAccount,
            BankAccount destinationAccount,
            BigDecimal amount,
            String currency) {

        if (sourceAccount.getIban().equals(destinationAccount.getIban())) {
            throw new IllegalArgumentException("Нельзя перевести деньги на тот же счёт");
        }

        try {

            sourceAccount.debit(amount, currency);
            destinationAccount.credit(amount, currency);

            paymentTransactionRepository.save(
                    PaymentTransaction.completedTransfer(
                            sourceAccount.getId(),
                            destinationAccount.getId(),
                            amount,
                            currency
                    )
            );

            return new MoneyTransferResult(
                    sourceAccount.getIban(),
                    destinationAccount.getIban(),
                    amount,
                    currency,
                    sourceAccount.getBalance()
            );

        } catch (RuntimeException ex) {

            paymentTransactionRepository.save(
                    PaymentTransaction.failedTransfer(
                            sourceAccount.getId(),
                            destinationAccount.getId(),
                            amount,
                            currency,
                            ex.getMessage()
                    )
            );

            throw ex;
        }
    }
}