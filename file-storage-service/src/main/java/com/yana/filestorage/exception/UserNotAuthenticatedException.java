package com.yana.filestorage.exception;

public class UserNotAuthenticatedException extends RuntimeException {

    public UserNotAuthenticatedException() {
        super();
    }

    public UserNotAuthenticatedException(String message) {
        super(message);
    }

    public UserNotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotAuthenticatedException(Throwable cause) {
        super(cause);
    }

    protected UserNotAuthenticatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
