package com.example.mobipay.domain.postpayments.error;

public class ReceiptUserMismatchException extends RuntimeException {

    public ReceiptUserMismatchException() {
    }

    public ReceiptUserMismatchException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
