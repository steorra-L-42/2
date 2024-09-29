package com.example.mobipay.domain.car.error;

public class NotMemberException extends RuntimeException {

    public NotMemberException() {
        super("차량 멤버가 아닙니다.");
    }

   public NotMemberException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
