package com.example.demo2.banking.application.command;

import java.math.BigDecimal;

public record TransferMoneyCommand(
        String fromIban,
        String toIban,
        BigDecimal amount,
        String currency
) {
}
