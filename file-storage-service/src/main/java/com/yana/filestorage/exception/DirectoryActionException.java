package com.yana.filestorage.exception;

public class DirectoryActionException extends RuntimeException {

    public DirectoryActionException() {
        super();
    }

    public DirectoryActionException(String message) {
        super(message);
    }

    public DirectoryActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectoryActionException(Throwable cause) {
        super(cause);
    }

    protected DirectoryActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
