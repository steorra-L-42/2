package com.example.mobipay.domain.cancel.error;

public class CancelServerException extends RuntimeException {
    public CancelServerException() {
        super("SSAFY API Server error occurred while cancelling transaction");
    }

    public CancelServerException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
