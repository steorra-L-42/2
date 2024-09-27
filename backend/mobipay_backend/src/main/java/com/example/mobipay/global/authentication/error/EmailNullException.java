package com.example.mobipay.global.authentication.error;

public class EmailNullException extends RuntimeException {

    public EmailNullException() {
        super();
    }

    public EmailNullException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

