package com.example.demo2.transaction.domain.model;

import lombok.Getter;

@Getter
public enum PaymentTransactionStatus {
    PROCESSING,
    SUCCESS,
    FAILED;

    public static PaymentTransactionStatus fromString(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("PaymentTransactionStatus не может быть пустым");
        }
        for (PaymentTransactionStatus paymentTransactionStatus : PaymentTransactionStatus.values()) {
            if (paymentTransactionStatus.name().equalsIgnoreCase(status.trim())) {
                return paymentTransactionStatus;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentTransactionStatus " + status);
    }

}
