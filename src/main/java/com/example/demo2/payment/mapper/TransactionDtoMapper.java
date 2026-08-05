package com.example.demo2.payment.mapper;

import com.example.demo2.payment.dto.TransferByCardCommand;
import com.example.demo2.payment.dto.TransferByIbanCommand;
import com.example.demo2.payment.dto.TransferByPhoneCommand;
import com.example.demo2.payment.dto.MoneyTransferResult;
import com.example.demo2.payment.dto.MoneyTransferResponse;
import com.example.demo2.payment.dto.TransferByCardRequest;
import com.example.demo2.payment.dto.TransferByIbanRequest;
import com.example.demo2.payment.dto.TransferByPhoneRequest;
import org.springframework.stereotype.Component;

@Component
public class TransactionDtoMapper {

    public TransferByIbanCommand toCommand(TransferByIbanRequest request) {
        return new TransferByIbanCommand(
                request.getFromIban(),
                request.getToIban(),
                request.getAmount(),
                request.getCurrency()
        );
    }

    public TransferByPhoneCommand toCommand(TransferByPhoneRequest request) {
        return new TransferByPhoneCommand(
                request.getFromIban(),
                request.getTargetPhone(),
                request.getAmount(),
                request.getCurrency()
        );
    }

    public TransferByCardCommand toCommand(TransferByCardRequest request) {
        return new TransferByCardCommand(
                request.getFromIban(),
                request.getTargetPan(),
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
