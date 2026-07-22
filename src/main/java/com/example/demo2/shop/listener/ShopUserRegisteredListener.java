package com.example.demo2.shop.listener;

import com.example.demo2.shared.event.UserRegisteredEvent;
import com.example.demo2.shop.entity.Customer;
import com.example.demo2.shop.repository.CustomerRepository;
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
public class ShopUserRegisteredListener {

    private final CustomerRepository customerRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("Создание профиля Customer для userId={}", event.userId());

        Customer customer = Customer.builder()
                .userId(event.userId())
                .bonusPoints(0)
                // address и phone пока null — клиент заполнит их при первом заказе
                .build();

        customerRepository.save(customer);
        log.info("Профиль Customer успешно создан с id={}", customer.getId());
    }
}
