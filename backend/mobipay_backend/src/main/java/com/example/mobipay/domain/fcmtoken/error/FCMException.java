package com.example.mobipay.domain.fcmtoken.error;

public class FCMException extends RuntimeException {
    public FCMException(String message) {
        super(message);
    }
    public FCMException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
