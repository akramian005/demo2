package com.example.demo2.card.domain.exception;

public class CurrencyMismatchException extends RuntimeException {
    public CurrencyMismatchException(String message) { super(message); }
}