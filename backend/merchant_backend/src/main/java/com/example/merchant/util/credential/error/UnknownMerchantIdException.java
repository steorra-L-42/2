package com.example.merchant.util.credential.error;

public class UnknownMerchantIdException extends RuntimeException {
    public UnknownMerchantIdException() {
        super("Unknown Merchant Id");
    }

    public UnknownMerchantIdException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
