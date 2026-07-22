package com.example.demo2.banking.domain.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) { super(message); }
}