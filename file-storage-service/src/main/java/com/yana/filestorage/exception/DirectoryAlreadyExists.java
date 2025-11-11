package com.yana.filestorage.exception;

public class DirectoryAlreadyExists extends RuntimeException {

    public DirectoryAlreadyExists() {
        super();
    }

    public DirectoryAlreadyExists(String message) {
        super(message);
    }

    public DirectoryAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectoryAlreadyExists(Throwable cause) {
        super(cause);
    }

    protected DirectoryAlreadyExists(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
