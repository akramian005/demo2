package com.example.demo2.identity.service;

import com.example.demo2.account.domain.repository.BankAccountRepository;
import com.example.demo2.identity.dto.RegisterRequest;
import com.example.demo2.identity.repository.UserRepository;
import com.example.demo2.shop.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthServiceRegistrationIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Test
    void registerCreatesCustomerAndBankAccount() {
        String email = "listener-" + UUID.randomUUID() + "@example.com";

        authService.register(new RegisterRequest("Test", "User", email, "password123"));

        var user = userRepository.findByEmail(email).orElseThrow();

        assertThat(customerRepository.findByUserId(user.getId())).isPresent();
        assertThat(bankAccountRepository.findAllByUserId(user.getId())).isNotEmpty();
    }
}
