package com.example.mobipay.domain.postpayments.error;

public class InvalidPaymentBalanceException extends RuntimeException {

    public InvalidPaymentBalanceException() {
    }

    public InvalidPaymentBalanceException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
