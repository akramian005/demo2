package com.example.demo2.banking.domain.exception;

public class CardNotUsableException extends RuntimeException {
    public CardNotUsableException(String message) { super(message); }
}