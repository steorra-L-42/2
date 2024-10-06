package com.example.mobipay.domain.postpayments.error;

public class NotEqualPaymentBalanceException extends RuntimeException {

    public NotEqualPaymentBalanceException() {
    }

    public NotEqualPaymentBalanceException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
