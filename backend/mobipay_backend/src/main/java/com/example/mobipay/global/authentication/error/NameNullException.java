package com.example.mobipay.global.authentication.error;

public class NameNullException extends RuntimeException {

    public NameNullException() {
        super();
    }

    public NameNullException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

