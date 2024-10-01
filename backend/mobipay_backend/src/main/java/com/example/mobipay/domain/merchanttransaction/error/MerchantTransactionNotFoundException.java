package com.example.mobipay.domain.merchanttransaction.error;

public class MerchantTransactionNotFoundException extends RuntimeException {

    public MerchantTransactionNotFoundException() {
    }

    public MerchantTransactionNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
