package com.example.merchant.domain.parking.error;

public class MultipleNotPaidException extends RuntimeException {

    public MultipleNotPaidException() {
        super("Multiple not paid parking");
    }

    public MultipleNotPaidException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
