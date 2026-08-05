package com.example.demo2.payment.dto;

import java.math.BigDecimal;

public record MoneyTransferResult(
        String fromIban,
        String toIban,
        BigDecimal amount,
        String currency,
        BigDecimal senderBalance
) {
}
