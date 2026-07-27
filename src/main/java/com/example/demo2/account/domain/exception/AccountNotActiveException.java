package com.example.demo2.account.domain.exception;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(String message) { super(message); }
}