package com.example.mobipay.domain.postpayments.error;

public class OneTimeLimitExceedException extends RuntimeException {

    public OneTimeLimitExceedException() {
    }

    public OneTimeLimitExceedException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
