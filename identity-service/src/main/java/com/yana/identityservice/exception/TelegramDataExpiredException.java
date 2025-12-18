package com.yana.identityservice.exception;

public class TelegramDataExpiredException extends RuntimeException {
    public TelegramDataExpiredException(String message) {
        super(message);
    }
}
