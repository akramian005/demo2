package com.example.demo2.banking.presentation.mapper;

import com.example.demo2.banking.application.command.TransferMoneyCommand;
import com.example.demo2.banking.application.result.MoneyTransferResult;
import com.example.demo2.banking.domain.model.BankAccount;
import com.example.demo2.banking.presentation.dto.AccountCheckResponse;
import com.example.demo2.banking.presentation.dto.MoneyTransferRequest;
import com.example.demo2.banking.presentation.dto.MoneyTransferResponse;
import org.springframework.stereotype.Component;

@Component
public class BankingDtoMapper {

    public TransferMoneyCommand toCommand(MoneyTransferRequest request) {
        return new TransferMoneyCommand(
                request.getFromIban(),
                request.getToIban(),
                request.getAmount(),
                request.getCurrency()
        );
    }

    public MoneyTransferResponse toResponse(MoneyTransferResult result) {
        return new MoneyTransferResponse(
                result.fromIban(),
                result.toIban(),
                result.amount(),
                result.currency(),
                result.senderBalance()
        );
    }

    public AccountCheckResponse toCheckResponse(BankAccount account) {
        return new AccountCheckResponse(
                true,
                account.getIban(),
                account.getCurrency(),
                account.getStatus().name()
        );
    }
}