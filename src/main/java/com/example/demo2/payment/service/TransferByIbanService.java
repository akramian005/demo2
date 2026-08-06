package com.example.demo2.payment.service;

import com.example.demo2.payment.dto.MoneyTransferResult;
import com.example.demo2.payment.dto.TransferByIbanRequest;
import com.example.demo2.payment.model.entity.BankAccount;
import com.example.demo2.payment.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TransferByIbanService {

    private final BankAccountRepository accountRepository;
    private final TransferExecutionService transferExecutionService;

    @Transactional
    public MoneyTransferResult transfer(Long currentUserId, TransferByIbanRequest request) {
        String fromIban = normalizeIban(request.getFromIban());
        String toIban = normalizeIban(request.getToIban());
        String currency = request.getCurrency().trim().toUpperCase();

        BankAccount sourceAccount = accountRepository.findByIban(fromIban)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт отправителя не найден"));

        if (!sourceAccount.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя переводить деньги с чужого счёта");
        }

        BankAccount destinationAccount = accountRepository.findByIban(toIban)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт получателя не найден"));

        return transferExecutionService.transfer(
                sourceAccount,
                destinationAccount,
                request.getAmount(),
                currency
        );
    }

    private String normalizeIban(String iban) {
        return iban.replaceAll("\\s+", "").toUpperCase();
    }
}