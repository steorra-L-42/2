package com.example.merchant.domain.payment.error;

public class InvalidMerchantTypeException extends  RuntimeException {
    public InvalidMerchantTypeException() {
        super("Invalid Merchant Type");
    }

   public InvalidMerchantTypeException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
