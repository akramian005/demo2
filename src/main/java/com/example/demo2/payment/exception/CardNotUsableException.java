package com.example.demo2.payment.exception;

public class CardNotUsableException extends RuntimeException {
    public CardNotUsableException(String message) { super(message); }
}