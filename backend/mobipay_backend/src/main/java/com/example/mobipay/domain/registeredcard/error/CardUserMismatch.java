package com.example.mobipay.domain.registeredcard.error;

public class CardUserMismatch extends RuntimeException {
    public CardUserMismatch() {
        super("You cannot access this card");
    }

    public CardUserMismatch(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
