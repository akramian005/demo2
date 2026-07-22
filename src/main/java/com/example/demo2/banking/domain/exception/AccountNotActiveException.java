package com.example.demo2.banking.domain.exception;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(String message) { super(message); }
}