package com.example.mobipay.domain.car.error;

public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException() {
        super();
    }

    public CarNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}