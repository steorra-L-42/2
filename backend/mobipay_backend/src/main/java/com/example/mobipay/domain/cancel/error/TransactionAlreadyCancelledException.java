package com.example.mobipay.domain.cancel.error;

public class TransactionAlreadyCancelledException extends RuntimeException {
    public TransactionAlreadyCancelledException() {
        super("Transaction is already cancelled");
    }

    public TransactionAlreadyCancelledException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
