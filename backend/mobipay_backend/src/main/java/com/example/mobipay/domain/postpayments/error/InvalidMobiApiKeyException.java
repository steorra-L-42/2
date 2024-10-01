package com.example.mobipay.domain.postpayments.error;

public class InvalidMobiApiKeyException extends RuntimeException {

    public InvalidMobiApiKeyException() {
    }

    public InvalidMobiApiKeyException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}