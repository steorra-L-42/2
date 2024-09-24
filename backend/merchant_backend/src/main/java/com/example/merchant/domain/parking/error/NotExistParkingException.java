package com.example.merchant.domain.parking.error;

public class NotExistParkingException extends RuntimeException {

    public NotExistParkingException() {
        super("Not exist parking");
    }

    public NotExistParkingException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
