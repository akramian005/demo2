package com.example.demo2.payment.mapper;

import com.example.demo2.payment.dto.MoneyTransferResponse;
import com.example.demo2.payment.dto.MoneyTransferResult;
import org.springframework.stereotype.Component;

@Component
public class TransactionDtoMapper {

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