package com.example.demo2.card.domain.model;

public enum CardStatus {
    PENDING,   // Выпускается
    ACTIVE,    // Активна
    BLOCKED,   // Заблокирована владельцем или банком
    EXPIRED    // Истёк срок действия
}