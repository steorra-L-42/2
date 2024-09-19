package com.example.mobipay.domain.car.error;

public class DuplicatedCarNumberException extends RuntimeException {

    public DuplicatedCarNumberException() {
        super();
    }

    public DuplicatedCarNumberException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
