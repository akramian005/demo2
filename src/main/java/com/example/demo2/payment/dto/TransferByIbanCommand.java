package com.example.demo2.payment.dto;

import java.math.BigDecimal;

public record TransferByIbanCommand(
        String fromIban,
        String toIban,
        BigDecimal amount,
        String currency
) {
}
