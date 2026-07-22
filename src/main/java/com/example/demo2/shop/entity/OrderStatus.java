package com.example.demo2.shop.entity;

public enum OrderStatus {
    PENDING,     // создан, ожидает обработки
    PAID,        // оплачен
    SHIPPED,     // отправлен
    DELIVERED,   // доставлен
    CANCELLED    // отменён
}
