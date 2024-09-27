package com.example.mobipay.global.authentication.error;

public class CardProductNotFoundException extends RuntimeException {

    public CardProductNotFoundException() {
        super();
    }

    public CardProductNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

