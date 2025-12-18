package com.yana.identityservice.exception;

public class HashNotFoundException extends RuntimeException {
    public HashNotFoundException(String message) {
        super(message);
    }
}
