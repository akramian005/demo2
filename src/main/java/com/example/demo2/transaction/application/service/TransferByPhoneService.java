package com.example.demo2.transaction.application.service;

import com.example.demo2.account.domain.model.BankAccount;
import com.example.demo2.account.domain.repository.BankAccountRepository;
import com.example.demo2.identity.entity.User;
import com.example.demo2.identity.repository.UserRepository;
import com.example.demo2.transaction.application.command.TransferByPhoneCommand;
import com.example.demo2.transaction.application.result.MoneyTransferResult;
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
    public MoneyTransferResult transfer(Long currentUserId, TransferByPhoneCommand command) {
        String fromIban = normalizeIban(command.fromIban());
        String targetPhone = normalizePhone(command.targetPhone());
        String currency = command.currency().trim().toUpperCase();

        BankAccount fromAccount = accountRepository.findByIbanForUpdate(fromIban)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт отправителя не найден"));

        if (!fromAccount.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя переводить деньги с чужого счёта");
        }

        User receiver = userRepository.findByPhone(targetPhone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Получатель с таким телефоном не найден"));

        BankAccount toAccount = accountRepository.findAllByUserId(receiver.getId()).stream()
                .filter(account -> account.getCurrency().equals(currency))
                .findFirst()
                .map(account -> accountRepository.findByIbanForUpdate(account.getIban()).orElseThrow())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт получателя в указанной валюте не найден"));

        return transferExecutionService.transfer(fromAccount, toAccount, command.amount(), currency);
    }

    private String normalizeIban(String iban) {
        return iban.replaceAll("\\s+", "").toUpperCase();
    }

    private String normalizePhone(String phone) {
        return phone.replaceAll("\\s+", "");
    }
}
