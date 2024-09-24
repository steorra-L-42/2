package com.example.merchant.domain.parking.error;

public class InvalidMerApiKeyException extends IllegalArgumentException {

    public InvalidMerApiKeyException() {
        super();
    }

    public InvalidMerApiKeyException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
