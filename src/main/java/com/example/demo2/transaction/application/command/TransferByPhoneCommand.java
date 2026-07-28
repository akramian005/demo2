package com.example.demo2.transaction.application.command;

import java.math.BigDecimal;

public record TransferByPhoneCommand(
        String fromIban,
        String targetPhone,
        BigDecimal amount,
        String currency
) {
}
