package com.example.mobipay.global.authentication.error;

public class NotExistingMobiUserException extends RuntimeException {

    public NotExistingMobiUserException() {
        super();
    }

    public NotExistingMobiUserException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
