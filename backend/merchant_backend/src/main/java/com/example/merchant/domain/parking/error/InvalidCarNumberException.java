package com.example.merchant.domain.parking.error;

public class InvalidCarNumberException extends  RuntimeException {

    public InvalidCarNumberException() {
        super("차량 번호는 7자리 이상 8자리 이하로 입력해주세요.");
    }

    public InvalidCarNumberException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
