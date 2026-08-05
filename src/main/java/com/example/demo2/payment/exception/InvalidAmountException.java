package com.example.demo2.payment.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) { super(message); }
}