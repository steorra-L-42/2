package com.example.mobipay.domain.invitation.error;

public class AlreadyInvitedException extends RuntimeException {

    public AlreadyInvitedException() {
        super("Already invited");
    }

    public AlreadyInvitedException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
