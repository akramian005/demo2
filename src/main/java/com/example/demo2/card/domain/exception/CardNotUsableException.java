package com.example.demo2.card.domain.exception;

public class CardNotUsableException extends RuntimeException {
    public CardNotUsableException(String message) { super(message); }
}