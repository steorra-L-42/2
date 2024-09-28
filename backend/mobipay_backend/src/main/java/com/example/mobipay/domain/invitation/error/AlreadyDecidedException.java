package com.example.mobipay.domain.invitation.error;

public class AlreadyDecidedException extends RuntimeException {

    public AlreadyDecidedException() {
        super("Already decided");
    }

    public AlreadyDecidedException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
