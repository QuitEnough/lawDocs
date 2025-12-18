package com.yana.identityservice.exception;

public class InvalidTelegramHash extends RuntimeException {
    public InvalidTelegramHash(String message) {
        super(message);
    }
}
