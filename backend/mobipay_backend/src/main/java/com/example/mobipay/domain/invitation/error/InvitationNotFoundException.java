package com.example.mobipay.domain.invitation.error;

public class InvitationNotFoundException extends RuntimeException {

    public InvitationNotFoundException() {
        super("Invitation not found");
    }

    public InvitationNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
