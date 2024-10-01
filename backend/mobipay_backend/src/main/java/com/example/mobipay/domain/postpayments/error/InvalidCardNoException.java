package com.example.mobipay.domain.postpayments.error;

public class InvalidCardNoException extends RuntimeException {

    public InvalidCardNoException() {
    }

    public InvalidCardNoException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
