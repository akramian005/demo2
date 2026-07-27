package com.example.demo2.account.presentation.dto;

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
