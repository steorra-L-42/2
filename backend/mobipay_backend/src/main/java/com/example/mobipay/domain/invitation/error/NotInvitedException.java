package com.example.mobipay.domain.invitation.error;

public class NotInvitedException extends RuntimeException {
    public NotInvitedException() {
        super("Not invited");
    }

    public NotInvitedException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
