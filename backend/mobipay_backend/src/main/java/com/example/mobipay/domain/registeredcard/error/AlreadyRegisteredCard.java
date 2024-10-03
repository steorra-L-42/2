package com.example.mobipay.domain.registeredcard.error;

public class AlreadyRegisteredCard extends RuntimeException {
    public AlreadyRegisteredCard() {
        super("Already Registered Card");
    }

    public AlreadyRegisteredCard(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}