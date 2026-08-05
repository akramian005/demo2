package com.example.demo2.payment.model.enums;

public enum CardStatus {
    PENDING,   // Выпускается
    ACTIVE,    // Активна
    BLOCKED,   // Заблокирована владельцем или банком
    EXPIRED    // Истёк срок действия
}