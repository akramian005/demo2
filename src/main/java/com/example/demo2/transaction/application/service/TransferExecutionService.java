package com.example.demo2.transaction.application.service;

import com.example.demo2.account.domain.exception.AccountNotActiveException;
import com.example.demo2.account.domain.exception.InsufficientFundsException;
import com.example.demo2.account.domain.exception.InvalidAmountException;
import com.example.demo2.account.domain.model.BankAccount;
import com.example.demo2.card.domain.exception.CurrencyMismatchException;
import com.example.demo2.transaction.application.result.MoneyTransferResult;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class TransferExecutionService {

    public MoneyTransferResult transfer(BankAccount fromAccount, BankAccount toAccount, BigDecimal amount, String currency) {
        if (fromAccount.getIban().equals(toAccount.getIban())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя перевести деньги на тот же счёт");
        }

        try {
            fromAccount.debit(amount, currency);
            toAccount.credit(amount, currency);
        } catch (AccountNotActiveException | CurrencyMismatchException |
                 InsufficientFundsException | InvalidAmountException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }

        return new MoneyTransferResult(
                fromAccount.getIban(),
                toAccount.getIban(),
                amount,
                currency,
                fromAccount.getBalance()
        );
    }
}
