package com.example.merchant.domain.cancel.error;

public class InvalidTransactionUniqueNoException extends RuntimeException {
    public InvalidTransactionUniqueNoException() {
        super("Invalid Transaction Unique No");
    }

    public InvalidTransactionUniqueNoException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
