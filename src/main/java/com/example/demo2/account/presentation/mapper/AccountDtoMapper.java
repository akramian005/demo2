package com.example.demo2.account.presentation.mapper;

import com.example.demo2.account.domain.model.BankAccount;
import com.example.demo2.account.presentation.dto.AccountCheckResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountDtoMapper {

    public AccountCheckResponse toCheckResponse(BankAccount account) {
        return new AccountCheckResponse(
                true,
                account.getIban(),
                account.getCurrency(),
                account.getStatus().name()
        );
    }
}