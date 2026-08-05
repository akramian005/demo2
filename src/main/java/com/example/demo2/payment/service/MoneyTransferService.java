package com.example.demo2.payment.service;

import com.example.demo2.payment.model.entity.BankAccount;
import com.example.demo2.payment.repository.BankAccountRepository;
import com.example.demo2.payment.dto.TransferByIbanCommand;
import com.example.demo2.payment.dto.MoneyTransferResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MoneyTransferService {

    private final BankAccountRepository accountRepository;
    private final TransferExecutionService transferExecutionService;

    @Transactional
    public MoneyTransferResult transfer(Long currentUserId, TransferByIbanCommand command) {
        String fromIban = normalizeIban(command.fromIban());
        String toIban = normalizeIban(command.toIban());
        String currency = command.currency().trim().toUpperCase();

        BankAccount sourceAccount = accountRepository.findByIbanForUpdate(fromIban)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт отправителя не найден"));

        if (!sourceAccount.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя переводить деньги с чужого счёта");
        }

        BankAccount destinationAccount = accountRepository.findByIbanForUpdate(toIban)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт получателя не найден"));

        return transferExecutionService.transfer(sourceAccount, destinationAccount, command.amount(), currency);
    }

    private String normalizeIban(String iban) {
        return iban.replaceAll("\\s+", "").toUpperCase();
    }
}
