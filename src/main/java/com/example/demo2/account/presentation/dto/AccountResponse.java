package com.example.demo2.account.presentation.dto;

import com.example.demo2.account.domain.model.AccountStatus;
import com.example.demo2.account.domain.model.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountResponse {

    private Long id;
    private Long userId;
    private String accountNumber;
    private String iban;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;

    public static AccountResponse from(BankAccount account) {
        return new AccountResponse(
                account.getId(),
                account.getUserId(),
                account.getAccountNumber(),
                account.getIban(),
                account.getBalance(),
                account.getCurrency(),
                account.getStatus()
        );
    }
}
