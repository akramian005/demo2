package com.example.demo2.transaction.application.service;

import com.example.demo2.account.domain.exception.AccountNotActiveException;
import com.example.demo2.account.domain.exception.InsufficientFundsException;
import com.example.demo2.account.domain.exception.InvalidAmountException;
import com.example.demo2.account.domain.model.BankAccount;
import com.example.demo2.card.domain.exception.CurrencyMismatchException;
import com.example.demo2.transaction.application.result.MoneyTransferResult;
import com.example.demo2.transaction.domain.model.PaymentTransaction;
import com.example.demo2.transaction.domain.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferExecutionService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    public MoneyTransferResult transfer(BankAccount sourceAccount, BankAccount destinationAccount, BigDecimal amount, String currency) {
        if (sourceAccount.getIban().equals(destinationAccount.getIban())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя перевести деньги на тот же счёт");
        }

        try {
            sourceAccount.debit(amount, currency);
            destinationAccount.credit(amount, currency);
        } catch (AccountNotActiveException | CurrencyMismatchException |
                 InsufficientFundsException | InvalidAmountException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }

        paymentTransactionRepository.save(PaymentTransaction.completedTransfer(
                sourceAccount.getId(),
                destinationAccount.getId(),
                amount,
                currency
        ));

        return new MoneyTransferResult(
                sourceAccount.getIban(),
                destinationAccount.getIban(),
                amount,
                currency,
                sourceAccount.getBalance()
        );
    }
}
