package com.example.demo2.banking.application.listener;

//package com.example.demo2.banking.application.listener;

import com.example.demo2.banking.application.service.OpenAccountService;
import com.example.demo2.shared.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankingUserRegisteredListener {

    private final OpenAccountService openAccountService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("Открытие банковского счёта для userId={}", event.userId());

        openAccountService.open(event.userId(), "KGS"); // валюта по умолчанию

        log.info("Банковский счёт успешно открыт для userId={}", event.userId());
    }
}
