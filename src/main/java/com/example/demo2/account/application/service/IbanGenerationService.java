package com.example.demo2.account.application.service;

import com.example.demo2.account.domain.model.Iban;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class IbanGenerationService {

    private static final String COUNTRY_CODE = "KG";
    private static final String BANK_CODE = "1234"; // 4 цифры кода банка
    private static final int ACCOUNT_NUMBER_LENGTH = 12; // Фиксированная длина номера счета

    /**
     * Генерирует валидный IBAN на основе номера счёта.
     * Контрольная сумма считается по алгоритму MOD-97 (ISO 7064).
     */
    public Iban generate(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Номер счёта не может быть пустым для генерации IBAN");
        }

        String cleanAccount = accountNumber.trim();

        // Выравниваем номер счета нулями слева до 12 символов
        String paddedAccount = padLeftWithZeros(cleanAccount, ACCOUNT_NUMBER_LENGTH);
        String bban = BANK_CODE + paddedAccount;

        String checkDigits = calculateCheckDigits(bban);
        String rawIban = COUNTRY_CODE + checkDigits + bban;

        return Iban.of(rawIban);
    }

    private String calculateCheckDigits(String bban) {
        String rearranged = bban + COUNTRY_CODE + "00";
        String numeric = convertToNumeric(rearranged);
        BigInteger number = new BigInteger(numeric);
        int remainder = number.mod(BigInteger.valueOf(97)).intValue();
        int checkDigits = 98 - remainder;
        return String.format("%02d", checkDigits);
    }

    private String convertToNumeric(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                // Character.getNumericValue('A') = 10, ..., 'Z' = 35
                sb.append(Character.getNumericValue(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String padLeftWithZeros(String input, int length) {
        if (input.length() >= length) {
            return input;
        }
        return String.format("%" + length + "s", input).replace(' ', '0');
    }
}