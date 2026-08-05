package com.example.demo2.payment.service;

import com.example.demo2.payment.model.entity.BankAccount;
import com.example.demo2.payment.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountQueryService {

    private final BankAccountRepository accountRepository;

    public List<BankAccount> getUserAccounts(Long userId) {
        return accountRepository.findAllByUserId(userId);
    }

    public Optional<BankAccount> findByIban(String iban) {
        return accountRepository.findByIban(iban);
    }
}
