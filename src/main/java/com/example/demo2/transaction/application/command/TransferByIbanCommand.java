package com.example.demo2.transaction.application.command;

import java.math.BigDecimal;

public record TransferByIbanCommand(
        String fromIban,
        String toIban,
        BigDecimal amount,
        String currency
) {
}
