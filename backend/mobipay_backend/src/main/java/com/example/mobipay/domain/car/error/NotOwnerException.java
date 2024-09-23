package com.example.mobipay.domain.car.error;

public class NotOwnerException extends RuntimeException {

    public NotOwnerException() {
        super();
    }

    public NotOwnerException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
