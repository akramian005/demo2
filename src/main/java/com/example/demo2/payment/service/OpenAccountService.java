package com.example.demo2.payment.service;

import com.example.demo2.identity.service.UserQueryService;
import com.example.demo2.payment.model.entity.BankAccount;
import com.example.demo2.payment.model.entity.Iban;
import com.example.demo2.payment.model.entity.PaymentCard;
import com.example.demo2.payment.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenAccountService {

    private static final String DEFAULT_CURRENCY = "KGS";

    private final BankAccountRepository accountRepository;
    private final IbanGenerationService ibanGenerationService;
    private final UserQueryService userQueryService;
    private final CardIssuanceService cardIssuanceService;

    @Transactional
    public BankAccount open(Long userId) {

        String accountNumber = generateAccountNumber();

        Iban iban = ibanGenerationService.generate(accountNumber);

        BankAccount account = BankAccount.open(
                userId,
                accountNumber,
                iban.getValue(),
                DEFAULT_CURRENCY
        );

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