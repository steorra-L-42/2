package com.example.mobipay.domain.invitation.error;

public class InvitationNoFoundException extends RuntimeException {

    public InvitationNoFoundException() {
        super("Invitation not found");
    }

    public InvitationNoFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
