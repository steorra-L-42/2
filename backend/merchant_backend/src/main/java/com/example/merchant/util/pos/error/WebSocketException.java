package com.example.merchant.util.pos.error;

public class WebSocketException extends RuntimeException {

    public WebSocketException() {
       super("WeSocket Error");
    }

    public WebSocketException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
