package com.example.demo2.transaction.presentation.mapper;

import com.example.demo2.transaction.application.command.TransferMoneyCommand;
import com.example.demo2.transaction.application.result.MoneyTransferResult;
import com.example.demo2.transaction.presentation.dto.MoneyTransferRequest;
import com.example.demo2.transaction.presentation.dto.MoneyTransferResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionDtoMapper {

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
}