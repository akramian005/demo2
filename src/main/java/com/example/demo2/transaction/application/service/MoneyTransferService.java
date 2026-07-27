package com.example.demo2.transaction.application.service;

import com.example.demo2.transaction.application.command.TransferMoneyCommand;
import com.example.demo2.transaction.application.result.MoneyTransferResult;
import com.example.demo2.account.domain.exception.AccountNotActiveException;
import com.example.demo2.card.domain.exception.CurrencyMismatchException;
import com.example.demo2.account.domain.exception.InsufficientFundsException;
import com.example.demo2.account.domain.exception.InvalidAmountException;
import com.example.demo2.account.domain.model.BankAccount;
import com.example.demo2.account.domain.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MoneyTransferService {

    private final BankAccountRepository accountRepository;

    @Transactional
    public MoneyTransferResult transfer(Long currentUserId, TransferMoneyCommand command) {
        String fromIban = normalizeIban(command.fromIban());
        String toIban = normalizeIban(command.toIban());
        String currency = command.currency().trim().toUpperCase();

        if (fromIban.equals(toIban)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя перевести деньги на тот же счёт");
        }

        BankAccount fromAccount = accountRepository.findByIbanForUpdate(fromIban)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт отправителя не найден"));

        if (!fromAccount.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя переводить деньги с чужого счёта");
        }

        BankAccount toAccount = accountRepository.findByIbanForUpdate(toIban)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт получателя не найден"));

        try {
            fromAccount.debit(command.amount(), currency);
            toAccount.credit(command.amount(), currency);
        } catch (AccountNotActiveException | CurrencyMismatchException |
                 InsufficientFundsException | InvalidAmountException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }

        return new MoneyTransferResult(
                fromAccount.getIban(),
                toAccount.getIban(),
                command.amount(),
                currency,
                fromAccount.getBalance()
        );
    }

    private String normalizeIban(String iban) {
        return iban.replaceAll("\\s+", "").toUpperCase();
    }
}
