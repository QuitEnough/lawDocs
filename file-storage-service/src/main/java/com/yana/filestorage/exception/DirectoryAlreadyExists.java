package com.yana.filestorage.exception;

public class DirectoryAlreadyExists extends RuntimeException {
    public DirectoryAlreadyExists(String message) {
        super(message);
    }
}
