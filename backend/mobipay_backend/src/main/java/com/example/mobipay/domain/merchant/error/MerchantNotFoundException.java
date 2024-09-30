package com.example.mobipay.domain.merchant.error;

public class MerchantNotFoundException extends RuntimeException {

    public MerchantNotFoundException() {
    }

    public MerchantNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}