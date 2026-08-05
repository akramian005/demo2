package com.example.demo2.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MoneyTransferResponse {

    private String fromIban;
    private String toIban;
    private BigDecimal amount;
    private String currency;
    private BigDecimal senderBalance;
}
