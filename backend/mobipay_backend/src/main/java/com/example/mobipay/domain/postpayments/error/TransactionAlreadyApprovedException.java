package com.example.mobipay.domain.postpayments.error;

public class TransactionAlreadyApprovedException extends RuntimeException {

    public TransactionAlreadyApprovedException() {
    }

    public TransactionAlreadyApprovedException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
