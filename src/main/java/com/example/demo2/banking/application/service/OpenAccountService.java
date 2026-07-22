package com.example.demo2.banking.application.service;

import com.example.demo2.banking.domain.model.BankAccount;
import com.example.demo2.banking.domain.model.PaymentCard;
import com.example.demo2.banking.domain.repository.BankAccountRepository;
import com.example.demo2.identity.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OpenAccountService {

    private final BankAccountRepository accountRepository;
    private final IbanGenerationService ibanGenerationService;
    private final UserQueryService userQueryService;

    @Transactional
    public BankAccount open(Long userId, String currency) {
        String accountNumber = generateAccountNumber();
        var iban = ibanGenerationService.generate(accountNumber);

        BankAccount account = BankAccount.open(userId, accountNumber, iban.getValue(), currency);

        // Сразу выпускаем карту к новому счёту — как это обычно происходит
        // при открытии счёта физлицу в банке
        String cardholderName = userQueryService.getFullName(userId);
        String fullCardNumber = generateCardNumber();
        PaymentCard card = PaymentCard.issue(fullCardNumber, cardholderName, LocalDate.now().plusYears(4));

        account.linkCard(card);

        // cascade = ALL на BankAccount.card гарантирует, что card сохранится вместе с account
        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        long nextNumber = accountRepository.count() + 1;
        return String.format("%016d", nextNumber);
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}