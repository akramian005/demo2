package com.example.demo2.payment.service;

import com.example.demo2.identity.entity.User;
import com.example.demo2.identity.repository.UserRepository;
import com.example.demo2.payment.dto.MoneyTransferResult;
import com.example.demo2.payment.dto.TransferByPhoneRequest;
import com.example.demo2.payment.model.entity.BankAccount;
import com.example.demo2.payment.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TransferByPhoneService {

    private final BankAccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransferExecutionService transferExecutionService;

    @Transactional
    public MoneyTransferResult transfer(Long currentUserId, TransferByPhoneRequest request) {
        String fromIban = normalizeIban(request.getFromIban());
        String targetPhone = normalizePhone(request.getTargetPhone());
        String currency = request.getCurrency().trim().toUpperCase();

        BankAccount sourceAccount = accountRepository.findByIban(fromIban)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт отправителя не найден"));

        if (!sourceAccount.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя переводить деньги с чужого счёта");
        }

        User receiver = userRepository.findByPhone(targetPhone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Получатель с таким телефоном не найден"));

        BankAccount destinationAccount = accountRepository.findAllByUserId(receiver.getId()).stream()
                .filter(account -> account.getCurrency().equals(currency))
                .findFirst()
                .map(account -> accountRepository.findByIban(account.getIban()).orElseThrow())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт получателя в указанной валюте не найден"));

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

    private String normalizePhone(String phone) {
        return phone.replaceAll("\\s+", "");
    }
}