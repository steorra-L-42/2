package com.example.mobipay.domain.mobiuser.error;

public class MobiUserNotFoundException extends RuntimeException {

    public MobiUserNotFoundException() {
        super();
    }

    public MobiUserNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
