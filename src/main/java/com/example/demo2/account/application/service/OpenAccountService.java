package com.example.demo2.account.application.service;

import com.example.demo2.account.domain.model.BankAccount;
import com.example.demo2.account.domain.repository.BankAccountRepository;
import com.example.demo2.card.application.service.CardIssuanceService;
import com.example.demo2.card.domain.model.PaymentCard;
import com.example.demo2.identity.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenAccountService {

    private final BankAccountRepository accountRepository;
    private final IbanGenerationService ibanGenerationService;
    private final UserQueryService userQueryService;
    private final CardIssuanceService cardIssuanceService;

    @Transactional
    public BankAccount open(Long userId, String currency) {
        String accountNumber = generateAccountNumber();
        var iban = ibanGenerationService.generate(accountNumber);

        BankAccount account = BankAccount.open(userId, accountNumber, iban.getValue(), currency);

        String cardholderName = userQueryService.getFullName(userId);
        PaymentCard card = cardIssuanceService.issueCard(cardholderName);

        account.linkCard(card);

        // cascade = ALL на BankAccount.card гарантирует, что card сохранится вместе с account
        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        long nextNumber = accountRepository.count() + 1;
        return String.format("%016d", nextNumber);
    }
}