package com.example.mobipay.domain.invitation.error;

public class NotApprovedOrRejectedException extends RuntimeException {

    public NotApprovedOrRejectedException() {
        super("Not approved or rejected");
    }

    public NotApprovedOrRejectedException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
