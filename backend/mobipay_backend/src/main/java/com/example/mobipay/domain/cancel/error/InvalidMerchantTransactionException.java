package com.example.mobipay.domain.cancel.error;

public class InvalidMerchantTransactionException extends RuntimeException {

    public InvalidMerchantTransactionException() {
        super("Invalid Merchant Transaction");
    }

    public InvalidMerchantTransactionException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
