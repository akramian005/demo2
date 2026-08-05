package com.example.demo2.payment.dto;

import java.math.BigDecimal;

public record TransferByPhoneCommand(
        String fromIban,
        String targetPhone,
        BigDecimal amount,
        String currency
) {
}
