package com.example.mobipay.domain.approvalwaiting.error;

public class ApprovalWaitingNotFoundException extends RuntimeException {

    public ApprovalWaitingNotFoundException() {
    }

    public ApprovalWaitingNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}

