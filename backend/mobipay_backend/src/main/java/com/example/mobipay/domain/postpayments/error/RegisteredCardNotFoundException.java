package com.example.mobipay.domain.postpayments.error;

public class RegisteredCardNotFoundException extends RuntimeException {

    public RegisteredCardNotFoundException() {
    }

    public RegisteredCardNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
