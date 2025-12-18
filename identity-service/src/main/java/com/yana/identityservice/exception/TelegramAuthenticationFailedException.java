package com.yana.identityservice.exception;

public class TelegramAuthenticationFailedException extends RuntimeException {
    public TelegramAuthenticationFailedException(String message) {
        super(message);
    }
}
