package com.example.demo2.banking.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountCheckResponse {

    private boolean exists;
    private String iban;
    private String currency;
    private String status;
}
