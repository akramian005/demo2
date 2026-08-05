package com.example.demo2.payment.mapper;

import com.example.demo2.payment.model.entity.BankAccount;
import com.example.demo2.payment.dto.AccountCheckResponse;
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