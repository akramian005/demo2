package com.example.demo2.payment.dto;

import java.math.BigDecimal;

public record TransferByCardCommand(
        String fromIban,
        String targetPan,
        BigDecimal amount,
        String currency
) {
}
