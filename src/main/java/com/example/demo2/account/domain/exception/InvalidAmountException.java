package com.example.demo2.account.domain.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) { super(message); }
}