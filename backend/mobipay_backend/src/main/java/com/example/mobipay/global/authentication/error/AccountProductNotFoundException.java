package com.example.mobipay.global.authentication.error;

public class AccountProductNotFoundException extends RuntimeException {

    public AccountProductNotFoundException() {
        super();
    }

    public AccountProductNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

