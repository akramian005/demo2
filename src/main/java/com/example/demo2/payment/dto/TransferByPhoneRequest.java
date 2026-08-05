package com.example.demo2.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferByPhoneRequest {

    @NotBlank(message = "IBAN отправителя обязателен")
    private String fromIban;

    @NotBlank(message = "Телефон получателя обязателен")
    private String targetPhone;

    @NotNull(message = "Сумма обязательна")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private BigDecimal amount;

    @NotBlank(message = "Валюта обязательна")
    private String currency;
}
