package com.example.mobipay.domain.ownedcard.error;

public class OwnedCardNotFoundException extends RuntimeException {

    public OwnedCardNotFoundException() {
        super("Not found owned card");
    }

    public OwnedCardNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
