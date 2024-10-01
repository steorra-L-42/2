package com.example.mobipay.domain.postpayments.error;

public class CardTransactionServerError extends RuntimeException {

    public CardTransactionServerError() {
    }

    public CardTransactionServerError(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
