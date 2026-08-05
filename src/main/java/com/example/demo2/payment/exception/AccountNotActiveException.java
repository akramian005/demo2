package com.example.demo2.payment.exception;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(String message) { super(message); }
}