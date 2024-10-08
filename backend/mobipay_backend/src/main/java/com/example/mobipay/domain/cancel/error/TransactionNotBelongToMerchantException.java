package com.example.mobipay.domain.cancel.error;

public class TransactionNotBelongToMerchantException extends RuntimeException {

    public TransactionNotBelongToMerchantException() {
        super("Transaction does not belong to merchant");
    }

    public TransactionNotBelongToMerchantException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
